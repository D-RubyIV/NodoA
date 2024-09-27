package org.example.demo.repository;

import org.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = """
            SELECT DISTINCT p FROM Product p
            LEFT JOIN FETCH p.productCategories bc
            LEFT JOIN FETCH bc.category
            WHERE
            (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND
            (:productCode IS NULL OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :productCode, '%')))
            AND
            (:createdFrom IS NULL OR p.createdDate >= :createdFrom)
            AND
            (:createdTo IS NULL OR p.createdDate <= :createdTo)
            AND
            (p.status = 'ACTIVE')
            AND
            (bc.status = 'ACTIVE')
            AND
            (:categoryCode IS NULL OR EXISTS(SELECT 1 FROM ProductCategory pcc JOIN pcc.category pccc WHERE pcc.product = p AND LOWER(pcc.status) LIKE 'active' AND LOWER(pccc.categoryCode) LIKE LOWER(CONCAT('%', :categoryCode, '%'))) )
            """)
    Page<Product> findAllByPageWithQuery(
            @Param("name") String name,
            @Param("productCode") String productCode,
            @Param("categoryCode") String categoryCode,
            @Param("createdFrom") LocalDate createdFrom,
            @Param("createdTo") LocalDate createdTo,
            Pageable pageable
    );

    @Query(value = """
                        SELECT p FROM Product p
                        LEFT JOIN FETCH p.productCategories bc
                        LEFT JOIN FETCH bc.category bcc
                        WHERE
                        (:id IS NULL OR p.id = :id)
                        AND
                        (p.status = 'ACTIVE')
                        AND
                        (bc.status = 'ACTIVE')
            """)
    Optional<Product> findByIdWithCategoryActive(@Param("id") Long id);
    Optional<Product> findByProductCode(String code);
}
