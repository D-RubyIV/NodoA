package org.example.demo.service;

import org.apache.coyote.BadRequestException;
import org.example.demo.dto.category.request.CategoryRequestDTO;
import org.example.demo.dto.category.response.CategoryResponseDTO;
import org.example.demo.entity.Category;
import org.example.demo.enums.EStatus;
import org.example.demo.exception.CustomExceptions;
import org.example.demo.mapper.category.request.CategoryRequestMapper;
import org.example.demo.mapper.category.response.CategoryResponseMapper;
import org.example.demo.repository.CategoryRepository;
import org.example.demo.utils.CustomStringUtil;
import org.example.demo.utils.FileUploadUtil;
import org.example.demo.utils.PageableObject;
import org.example.demo.utils.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class CategoryService implements IService<Category, Long, CategoryRequestDTO, CategoryResponseDTO> {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private CategoryRequestMapper categoryRequestMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CategoryResponseMapper categoryResponseMapper;

    @Autowired
    private Translator translator;

    @Autowired
    private CustomStringUtil customStringUtil;

    public Page<CategoryResponseDTO> findAllOverviewByPage(
            String name,
            String code,
            LocalDate createdFrom,
            LocalDate createdTo,
            PageableObject pageableObject
    ) {
        name = customStringUtil.formatSearchParam(name);
        code = customStringUtil.formatSearchParam(code);
        if(pageableObject != null){
            Pageable pageable = pageableObject.toPageRequest();
            String query = pageableObject.getQuery();
            System.out.println("NAME: " + name);
            return categoryRepository.findAllByPageWithQuery(name, code, createdFrom, createdTo, pageable).map(s -> categoryResponseMapper.toDTO(s));
        }
        else {
            return categoryRepository.findAllByPageWithQuery(name, code, createdFrom, createdTo, null).map(s -> categoryResponseMapper.toDTO(s));
        }
    }

    public List<CategoryResponseDTO> findAllSortCode(Sort sort) {
        return categoryResponseMapper.toListDTO(categoryRepository.findAll(sort));
    }

    @Override
    public List<CategoryResponseDTO> findAll() {
        return categoryResponseMapper.toListDTO(categoryRepository.findAll());
    }

    @Override
    public CategoryResponseDTO findById(Long aLong) {
        return categoryResponseMapper.toDTO(categoryRepository.findById(aLong).orElseThrow(() -> new CustomExceptions.CustomBadRequest(translator.toLocale("NotFound", new Object[]{translator.toLocale("Category")}))));
    }

    public Category findEntityById(Long aLong){
        return categoryRepository.findById(aLong).orElseThrow(() -> new CustomExceptions.CustomBadRequest(translator.toLocale("NotFound", new Object[]{translator.toLocale("Category")})));
    }

    @Override
    @Transactional
    public CategoryResponseDTO softDelete(Long aLong) throws CustomExceptions.CustomBadRequest {
        Category category = findEntityById(aLong);
        if (category.getStatus() == EStatus.INACTIVE){
            throw new CustomExceptions.CustomBadRequest(translator.toLocale("DeletedBefore", new Object[]{translator.toLocale("Category")}));
        }
        else {
            category.setStatus(EStatus.INACTIVE);
            return categoryResponseMapper.toDTO(categoryRepository.save(category));
        }
    }

    @Override
    @Transactional
    public CategoryResponseDTO save(CategoryRequestDTO requestDTO){
        Category category = categoryRequestMapper.toEntity(requestDTO);
        if (categoryRepository.findByCategoryCode(requestDTO.getCategoryCode()).isPresent()) {
            throw new CustomExceptions.CustomBadRequest(translator.toLocale("ExistCodeException"));
        } else {
            if (requestDTO.getFile() != null) {
                try {
                    String nameImage = fileUploadUtil.saveFile("", requestDTO.getFile(), "");
                    category.setImage(nameImage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            category.setStatus(EStatus.ACTIVE);
            return categoryResponseMapper.toDTO(categoryRepository.save(category));
        }
    }

    @Override
    @Transactional
    public CategoryResponseDTO update(Long aLong, CategoryRequestDTO requestDTO) {
        Category category = findEntityById(aLong);
        if (category.getStatus() == EStatus.INACTIVE){
            throw new CustomExceptions.CustomBadRequest(translator.toLocale("DeletedBefore", new Object[]{translator.toLocale("Category")}));
        }
        if (requestDTO.getFile() != null) {
            try {
                if (category.getImage() != null){
                    fileUploadUtil.deleteFile(category.getImage());
                }
                String nameImage = fileUploadUtil.saveFile("", requestDTO.getFile(), "");
                category.setImage(nameImage);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        category.setName(requestDTO.getName());
        category.setStatus(requestDTO.getStatus());
        category.setDescription(requestDTO.getDescription());
        return categoryResponseMapper.toDTO(categoryRepository.save(category));
    }
}
