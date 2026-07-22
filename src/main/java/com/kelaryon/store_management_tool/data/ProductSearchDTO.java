package com.kelaryon.store_management_tool.data;

import java.math.BigDecimal;

public record ProductSearchDTO(
        String name,
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean sellable
) {
}
