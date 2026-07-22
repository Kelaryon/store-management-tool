package com.kelaryon.store_management_tool.mappers;

import com.kelaryon.store_management_tool.data.Product;
import com.kelaryon.store_management_tool.data.ProductDTO;
import com.kelaryon.store_management_tool.data.ProductDetail;
import com.kelaryon.store_management_tool.data.ProductDetailDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductDTOMapper {

    public ProductDTO mapSimple(Product product) {
        return baseBuilder(product)
                .build();
    }

    public ProductDTO mapFull(Product product) {
        return baseBuilder(product)
                .productDetails(
                        product.getProductDetails()
                                .stream()
                                .map(this::mapDetail)
                                .toList()
                )
                .build();
    }

    private ProductDTO.ProductDTOBuilder baseBuilder(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .addedAt(product.getAddedAt())
                .updatedAt(product.getUpdatedAt())
                .category(product.getCategory())
                .sellable(product.isSellable());
    }

    private ProductDetailDTO mapDetail(ProductDetail detail) {
        return ProductDetailDTO.builder()
                .id(detail.getId())
                .detailName(detail.getDetailName())
                .detail(detail.getDetail())
                .build();
    }
}
