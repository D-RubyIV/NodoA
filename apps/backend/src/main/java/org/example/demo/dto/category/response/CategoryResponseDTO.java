package org.example.demo.dto.category.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.demo.dto.BaseDTO;
import org.example.demo.enums.EStatus;

import java.time.LocalDate;
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryResponseDTO extends BaseDTO {
    private Long id;
    private String name;
    private String createdBy;
    private String modifiedBy;
    private String description;
    private String categoryCode;
    private String image;
    private EStatus status;
    private LocalDate createdDate;
    private LocalDate modifiedDate;
}
