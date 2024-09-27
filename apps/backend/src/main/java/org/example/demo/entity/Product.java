package org.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.demo.enums.EStatus;

import java.util.List;

@EqualsAndHashCode(callSuper = true, exclude = "productCategories")
@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(columnNames = {"code", "id"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "image")
    private String image;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EStatus status;

    @OneToMany(mappedBy = "product")
    @ToString.Exclude
    private List<ProductCategory> productCategories;


}
