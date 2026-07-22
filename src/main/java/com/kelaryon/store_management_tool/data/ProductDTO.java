package com.kelaryon.store_management_tool.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private Instant addedAt;
    private Instant updatedAt;
    private String category;
    private boolean sellable;
    private List<ProductDetailDTO> productDetails = new ArrayList<>();
}
