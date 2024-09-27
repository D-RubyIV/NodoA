package org.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.demo.enums.EStatus;

import java.util.List;

@EqualsAndHashCode(callSuper = true, exclude = "productCategories")
@Entity
@Table(name = "category", uniqueConstraints = @UniqueConstraint(columnNames = {"code","id"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "category_code")
    private String categoryCode;

    @Column(name = "image")
    private String image;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EStatus status;

    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    private List<ProductCategory> productCategories;
}
