package org.example.demo.service;

import org.apache.coyote.BadRequestException;
import org.example.demo.dto.category.request.CategoryRequestDTO;
import org.example.demo.dto.category.response.CategoryResponseDTO;
import org.example.demo.entity.Category;
import org.example.demo.enums.EStatus;
import org.example.demo.exception.CustomExceptions;
import org.example.demo.mapper.category.request.CategoryRequestMapper;
import org.example.demo.repository.CategoryRepository;
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
public class CategoryService implements IService<Category, Long, CategoryRequestDTO> {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private CategoryRequestMapper categoryRequestMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Translator translator;

    public Page<Category> findAllOverviewByPage(
            String name,
            String code,
            LocalDate createdFrom,
            LocalDate createdTo,
            PageableObject pageableObject
    ) {
        if(pageableObject != null){
            Pageable pageable = pageableObject.toPageRequest();
            String query = pageableObject.getQuery();
            return categoryRepository.findAllByPageWithQuery(name, code, createdFrom, createdTo, pageable);
        }
        else {
            return categoryRepository.findAllByPageWithQuery(name, code, createdFrom, createdTo, null);
        }
    }

    public List<Category> findAllSortCode(Sort sort) {
        return categoryRepository.findAll(sort);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long aLong) throws BadRequestException {
        return categoryRepository.findById(aLong).orElseThrow(() -> new BadRequestException(translator.toLocale("NotFound", new Object[]{translator.toLocale("Category")})));
    }

    @Override
    @Transactional(rollbackFor = BadRequestException.class)
    public Category softDelete(Long aLong) throws BadRequestException {
        Category category = findById(aLong);
        category.setStatus(EStatus.INACTIVE);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(rollbackFor = BadRequestException.class)
    public Category save(CategoryRequestDTO requestDTO) throws BadRequestException {
        Category category = categoryRequestMapper.toEntity(requestDTO);
        if (categoryRepository.findByCategoryCode(requestDTO.getCategoryCode()).isPresent()) {
            throw new BadRequestException(translator.toLocale("ExistCodeException"));
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
            return categoryRepository.save(category);
        }
    }

    @Override
    @Transactional(rollbackFor = BadRequestException.class)
    public Category update(Long aLong, CategoryRequestDTO requestDTO) throws BadRequestException {
        Category category = findById(aLong);
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
        return categoryRepository.save(category);
    }
}
