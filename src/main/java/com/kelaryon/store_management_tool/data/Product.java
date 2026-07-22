package com.kelaryon.store_management_tool.data;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(unique = true)
    private String name;
    @Column(nullable = false)
    @Positive
    private BigDecimal price;
    private String imageUrl;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant addedAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
    @NotBlank
    private String category;
    private boolean isSellable;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ProductDetail> productDetails = new ArrayList<>();

}
