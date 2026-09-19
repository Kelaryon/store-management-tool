package com.kelaryon.store_management_tool.repository;

import com.kelaryon.store_management_tool.data.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("""
                SELECT product FROM Product product
                LEFT JOIN FETCH product.productDetails
                WHERE product.id = :id
            """)
    Optional<Product> findProductWithDetails(@Param("id") Long id);

    boolean existsByName(@Param("name") String name);

    @Query("""
            SELECT COUNT(product) > 0
            FROM Product product
            WHERE product.name = :name
              AND product.id NOT IN :ids
            """)
    boolean existsByNameAndIdNotIn(@Param("name") String name,@Param("ids") Set<Long> ids);
}
