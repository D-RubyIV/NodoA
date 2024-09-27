package org.example.demo.mapper.category.request;

import org.example.demo.dto.category.request.CategoryRequestDTO;
import org.example.demo.entity.Category;
import org.example.demo.mapper.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryRequestMapper extends IMapper<Category, CategoryRequestDTO> {
}
