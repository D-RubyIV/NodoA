package org.example.demo.service;

import org.apache.coyote.BadRequestException;
import org.example.demo.dto.product.request.ProductRequestDTO;
import org.example.demo.entity.Category;
import org.example.demo.entity.Product;
import org.example.demo.entity.ProductCategory;
import org.example.demo.enums.EStatus;
import org.example.demo.mapper.product.request.ProductRequestMapper;
import org.example.demo.repository.CategoryRepository;
import org.example.demo.repository.ProductCategoryRepository;
import org.example.demo.repository.ProductRepository;
import org.example.demo.utils.FileUploadUtil;
import org.example.demo.utils.PageableObject;
import org.example.demo.utils.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductService implements IService<Product, Long, ProductRequestDTO> {

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
    private Translator translator;

    public Page<Product> findAllOverviewByPage(
            String name,
            String productCode,
            String categoryCode,
            LocalDate createdFrom,
            LocalDate createdTo,
            PageableObject pageableObject
    ) {
        if (pageableObject != null) {
            {
                Pageable pageable = pageableObject.toPageRequest();
                String query = pageableObject.getQuery();
                return productRepository.findAllByPageWithQuery(name, productCode, categoryCode, createdFrom, createdTo, pageable);
            }
        } else {
            return productRepository.findAllByPageWithQuery(name, productCode, categoryCode, createdFrom, createdTo, null);
        }
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long aLong) throws BadRequestException {
        return productRepository.findByIdWithCategoryActive(aLong).orElseThrow(() -> new BadRequestException(translator.toLocale("NotFound", new Object[]{translator.toLocale("Product")})));
    }

    @Override
    @Transactional(rollbackFor = BadRequestException.class)
    public Product softDelete(Long aLong) throws BadRequestException {
        Product product = findById(aLong);
        product.setStatus(EStatus.INACTIVE);
        return productRepository.save(product);
    }

    @Override
    @Transactional(rollbackFor = BadRequestException.class)
    public Product save(ProductRequestDTO requestDTO) throws BadRequestException {
        Product product = productRequestMapper.toEntity(requestDTO);
        // Check trùng mã
        if (productRepository.findByProductCode(requestDTO.getProductCode()).isPresent()) {
            throw new BadRequestException(translator.toLocale("ExistCodeException"));
        } else {
            Product productSaved = productRepository.save(product);
            // Get list category
            List<Category> categoriesInRequest = getAndValidateCategories(requestDTO);
            List<ProductCategory> productCategories = new ArrayList<>();
            categoriesInRequest.forEach(category -> {
                ProductCategory productCategory = new ProductCategory();
                productCategory.setProduct(productSaved);
                productCategory.setCategory(category);
                productCategory.setStatus(EStatus.ACTIVE);
                productCategories.add(productCategoryRepository.save(productCategory));
            });
            product.setStatus(EStatus.ACTIVE);
            product.setProductCategories(productCategories);
            if (requestDTO.getFile() != null) {
                try {
                    String nameImage = fileUploadUtil.saveFile("", requestDTO.getFile(), "");
                    product.setImage(nameImage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return productRepository.save(product);
        }
    }

    @Override
    @Transactional(rollbackFor = BadRequestException.class)
    public Product update(Long aLong, ProductRequestDTO requestDTO) throws BadRequestException {
        Product product = findById(aLong);
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
        product.setProductCategories(productCategoryRepository.saveAll(list));
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
        return productRepository.save(product);
    }

    private List<Category> getAndValidateCategories(ProductRequestDTO requestDTO) throws BadRequestException {
        List<Category> categoriesInRequest = categoryRepository.findAllByCategoryCodeIn(requestDTO.getCategoryCodes());
        if (categoriesInRequest.isEmpty()) {
            throw new BadRequestException(translator.toLocale("NoHaveAnyCategoryExist"));
        }
        return categoriesInRequest;
    }
}
