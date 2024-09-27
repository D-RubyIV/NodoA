package org.example.demo.dto.product.response;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.demo.dto.BaseDTO;
import org.example.demo.entity.ProductCategory;
import org.example.demo.enums.EStatus;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponseDTO extends BaseDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String productCode;
    private Long quantity;
    private String image;
    @Enumerated(EnumType.STRING)
    private EStatus status;
    private String categoryCodes;
}
