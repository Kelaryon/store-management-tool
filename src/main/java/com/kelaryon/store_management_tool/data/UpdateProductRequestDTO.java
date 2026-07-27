package com.kelaryon.store_management_tool.data;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateProductRequestDTO(
    String name,
    @Positive
    BigDecimal price,
    String category,
    String imageUrl,
    Boolean sellable
){}
