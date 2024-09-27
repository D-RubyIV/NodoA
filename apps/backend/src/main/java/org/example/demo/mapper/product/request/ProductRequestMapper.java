package org.example.demo.mapper.product.request;

import org.example.demo.dto.category.request.CategoryRequestDTO;
import org.example.demo.dto.product.request.ProductRequestDTO;
import org.example.demo.entity.Category;
import org.example.demo.entity.Product;
import org.example.demo.mapper.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductRequestMapper extends IMapper<Product, ProductRequestDTO> {
}
