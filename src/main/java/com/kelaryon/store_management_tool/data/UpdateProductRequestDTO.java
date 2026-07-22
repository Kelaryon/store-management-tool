package com.kelaryon.store_management_tool.data;

import java.math.BigDecimal;

public record UpdateProductRequestDTO(
    String name,
    BigDecimal price,
    String category,
    String imageUrl,
    Boolean sellable
){}
