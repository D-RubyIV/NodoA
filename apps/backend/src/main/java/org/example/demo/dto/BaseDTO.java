package org.example.demo.dto;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Data
@MappedSuperclass
public class BaseDTO {
    private LocalDate createdDate;
    private LocalDate modifiedDate;
    private String createdBy;
    private String modifiedBy;
}
