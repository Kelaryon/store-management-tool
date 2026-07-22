package com.kelaryon.store_management_tool.data;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Entity
@Table(
        name = "product_detail",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_detail_name",
                        columnNames = {"product_id", "detail_name"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String detailName;
    @NotBlank
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
