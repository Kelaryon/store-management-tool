package com.kelaryon.store_management_tool.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequestDTO(

    @NotBlank
    String name,
    @Positive
    BigDecimal price,
    @NotBlank
    String category,
    String imageUrl,
    List<ProductDetailRequestDTO>details
){}
