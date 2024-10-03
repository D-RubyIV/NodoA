package org.example.demo.service;

import org.example.demo.dto.product.request.ProductRequestDTO;
import org.example.demo.dto.product.response.ProductResponseDTO;
import org.example.demo.entity.Category;
import org.example.demo.entity.Product;
import org.example.demo.entity.ProductCategory;
import org.example.demo.enums.EStatus;
import org.example.demo.exception.CustomExceptions;
import org.example.demo.mapper.product.request.ProductRequestMapper;
import org.example.demo.mapper.product.response.ProductResponseMapper;
import org.example.demo.repository.CategoryRepository;
import org.example.demo.repository.ProductCategoryRepository;
import org.example.demo.repository.ProductRepository;
import org.example.demo.utils.CustomStringUtil;
import org.example.demo.utils.FileUploadUtil;
import org.example.demo.utils.PageableObject;
import org.example.demo.utils.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class ProductService implements IService<Product, Long, ProductRequestDTO, ProductResponseDTO> {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ProductRequestMapper productRequestMapper;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductResponseMapper productResponseMapper;

    @Autowired
    private Translator translator;

    @Autowired
    private CustomStringUtil customStringUtil;

    public Page<ProductResponseDTO> findAllOverviewByPage(
            String name,
            String productCode,
            String categoryCode,
            LocalDate createdFrom,
            LocalDate createdTo,
            PageableObject pageableObject
    ) {
        name = customStringUtil.formatSearchParam(name);
        productCode = customStringUtil.formatSearchParam(productCode);
        categoryCode = customStringUtil.formatSearchParam(categoryCode);
        if (pageableObject != null) {
            {
                Pageable pageable = pageableObject.toPageRequest();
                String query = pageableObject.getQuery();
                System.out.println("NAME: " + name);
                System.out.println("productCode: " + productCode);
                System.out.println("categoryCode: " + categoryCode);
                return productRepository.findAllByPageWithQuery(name, productCode, categoryCode, createdFrom, createdTo, pageable).map(s -> productResponseMapper.toDTO(s));
            }
        } else {
            return productRepository.findAllByPageWithQuery(name, productCode, categoryCode, createdFrom, createdTo, null).map(s -> productResponseMapper.toDTO(s));
        }
    }

    @Override
    public List<ProductResponseDTO> findAll() {
        return productResponseMapper.toListDTO(productRepository.findAll());
    }

    @Override
    public ProductResponseDTO findById(Long aLong) {
        return productResponseMapper.toDTO(productRepository.findByIdWithHQL(aLong).orElseThrow(() -> new CustomExceptions.CustomBadRequest(translator.toLocale("NotFound", new Object[]{translator.toLocale("Product")}))));
    }

    public Product findEntityById(Long aLong) {
        return productRepository.findByIdWithHQL(aLong).orElseThrow(() -> new CustomExceptions.CustomBadRequest(translator.toLocale("NotFound", new Object[]{translator.toLocale("Product")})));
    }

    @Override
    @Transactional
    public ProductResponseDTO softDelete(Long aLong){
        Product product = findEntityById(aLong);
        if (product.getStatus() == EStatus.INACTIVE){
            throw new CustomExceptions.CustomBadRequest(translator.toLocale("DeletedBefore", new Object[]{translator.toLocale("Product")}));
        }
        else{
            product.setStatus(EStatus.INACTIVE);
            return productResponseMapper.toDTO(productRepository.save(product));
        }
    }

    @Override
    @Transactional
    public ProductResponseDTO save(ProductRequestDTO requestDTO) {
        Product product = productRequestMapper.toEntity(requestDTO);
        // Check trùng mã
        if (productRepository.findByProductCode(requestDTO.getProductCode()).isPresent()) {
            throw new CustomExceptions.CustomBadRequest(translator.toLocale("ExistCodeException"));
        } else {
//            Product productSaved = productRepository.save(product);
            Product productSaved =  product;
            // Get list category
            List<Category> categoriesInRequest = getAndValidateCategories(requestDTO);
            List<ProductCategory> productCategories = new ArrayList<>();
            categoriesInRequest.forEach(category -> {
                ProductCategory productCategory = new ProductCategory();
                productCategory.setProduct(productSaved);
                productCategory.setCategory(category);
                productCategory.setStatus(EStatus.ACTIVE);
                System.out.println("------");
                productCategories.add(productCategory);
            });
            product.setStatus(EStatus.ACTIVE);
            List<ProductCategory> productCategoriesSaved = productCategoryRepository.saveAll(productCategories);
            product.setProductCategories(productCategoriesSaved);
            System.out.println("++++++");
            if (requestDTO.getFile() != null) {
                try {
                    String nameImage = fileUploadUtil.saveFile("", requestDTO.getFile(), "");
                    product.setImage(nameImage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return productResponseMapper.toDTO(productRepository.save(product));
        }
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long aLong, ProductRequestDTO requestDTO) {
        Product product = findEntityById(aLong);
        if (product.getStatus() == EStatus.INACTIVE){
            throw new CustomExceptions.CustomBadRequest(translator.toLocale("DeletedBefore", new Object[]{translator.toLocale("Product")}));
        }
        List<Category> categoriesInRequest = getAndValidateCategories(requestDTO);

        List<ProductCategory> productCategoriesInObject = product.getProductCategories();
        List<Category> categoriesInObject = productCategoriesInObject.stream().map(ProductCategory::getCategory).toList();
        // 124   OBJECT
        // 134  REQUEST
        List<ProductCategory> list = new ArrayList<>();
        categoriesInRequest.forEach(category -> {
            if (!categoriesInObject.contains(category)) {
                ProductCategory productCategory = new ProductCategory();
                productCategory.setProduct(product);
                productCategory.setCategory(category);
                productCategory.setStatus(EStatus.ACTIVE);
                list.add(productCategory);
            }
        });
        categoriesInObject.forEach(category -> {
            ProductCategory productCategory = productCategoriesInObject.stream().filter(s -> s.getCategory().equals(category)).findAny().get();
            if (!categoriesInRequest.contains(category)) {
                productCategory.setStatus(EStatus.INACTIVE);
            } else {
                productCategory.setStatus(EStatus.ACTIVE);
            }
            list.add(productCategory);
        });
        product.setName(requestDTO.getName());
        product.setStatus(requestDTO.getStatus());
        product.setQuantity(requestDTO.getQuantity());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setProductCategories(list);
        if (requestDTO.getFile() != null) {
            try {
                if (product.getImage() != null) {
                    fileUploadUtil.deleteFile(product.getImage());
                }
                String nameImage = fileUploadUtil.saveFile("", requestDTO.getFile(), "");
                product.setImage(nameImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return productResponseMapper.toDTO(productRepository.save(product));
    }

    private List<Category> getAndValidateCategories(ProductRequestDTO requestDTO) {
        List<Category> categoriesInRequest = categoryRepository.findAllByCategoryCodeIn(requestDTO.getCategoryCodes());
//        if (categoriesInRequest.isEmpty()) {
//            throw new CustomExceptions.CustomBadRequest(translator.toLocale("NoHaveAnyCategoryExist"));
//        }
        return categoriesInRequest;
    }
}
