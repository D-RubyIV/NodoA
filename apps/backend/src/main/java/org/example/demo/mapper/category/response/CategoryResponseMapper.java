package org.example.demo.mapper.category.response;

import org.example.demo.dto.category.request.CategoryRequestDTO;
import org.example.demo.dto.category.response.CategoryResponseDTO;
import org.example.demo.entity.Category;
import org.example.demo.mapper.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryResponseMapper extends IMapper<Category, CategoryResponseDTO> {
}
