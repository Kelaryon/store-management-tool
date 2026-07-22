package com.kelaryon.store_management_tool.repository;

import com.kelaryon.store_management_tool.data.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("""
                SELECT product FROM Product product
                LEFT JOIN FETCH product.productDetails
                WHERE product.id = :id
            """)
    Optional<Product> findProductWithDetails(Long id);
}
