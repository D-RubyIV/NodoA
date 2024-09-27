package org.example.demo.repository;

import org.example.demo.entity.Category;
import org.example.demo.enums.EStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = """
            SELECT DISTINCT c FROM Category c
            LEFT JOIN FETCH c.productCategories bc
            WHERE
            (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND
            (:code IS NULL OR LOWER(c.categoryCode) LIKE LOWER(CONCAT('%', :code, '%')))
            AND
            (:createdFrom IS NULL OR c.createdDate >= :createdFrom)
            AND
            (:createdTo IS NULL OR c.createdDate <= :createdTo)
            AND
            (c.status = 'ACTIVE')
            """)
    Page<Category> findAllByPageWithQuery(
            @Param("name") String name,
            @Param("code") String code,
            @Param("createdFrom") LocalDate createdFrom,
            @Param("createdTo") LocalDate createdTo,
            Pageable pageable
    );

    Optional<Category> findByCategoryCode(String code);

    List<Category> findAllByCategoryCodeIn(List<String> codes);
}
