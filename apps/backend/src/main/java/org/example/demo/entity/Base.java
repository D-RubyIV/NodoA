package org.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@MappedSuperclass
public class Base {
    @Column(name = "createdDate")
    @CreationTimestamp
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate createdDate;

    @Column(name = "modifiedDate")
    @UpdateTimestamp
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate modifiedDate;

    @Column(name = "createdBy")
    private String createdBy = "Admin";

    @Column(name = "modifiedBy")
    private String modifiedBy = "Admin";
}
