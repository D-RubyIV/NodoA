package org.example.demo.mapper.product.response;

import org.example.demo.dto.product.response.ProductResponseDTO;
import org.example.demo.entity.Category;
import org.example.demo.entity.Product;
import org.example.demo.entity.ProductCategory;
import org.example.demo.mapper.IMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductResponseMapper extends IMapper<Product, ProductResponseDTO> {

    @AfterMapping
    default void afterMapDTO(@MappingTarget ProductResponseDTO productResponseDTO, Product product ) {
        // Set List Course
        if (product.getProductCategories() != null){
            List<Category> listCategories = product.getProductCategories().stream().map(ProductCategory::getCategory).toList();
            if (!listCategories.isEmpty()){
                // Set Course Code
                List<String> listCode = listCategories.stream().map(Category::getCategoryCode).toList();
                if (!listCode.isEmpty()){
                    productResponseDTO.setCategoryCodes(String.join(", ", listCode));
                }
            }
        }
    }
}
