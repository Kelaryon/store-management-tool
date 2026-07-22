package com.kelaryon.store_management_tool.services;

import com.kelaryon.store_management_tool.data.*;
import com.kelaryon.store_management_tool.mappers.ProductDTOMapper;
import com.kelaryon.store_management_tool.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDTOMapper productDTOMapper;

    public ProductService(ProductRepository productRepository, ProductDTOMapper productDTOMapper) {
        this.productRepository = productRepository;
        this.productDTOMapper = productDTOMapper;
    }

    @Transactional
    public ProductDTO getProductById(Long id) {
        return productRepository.findProductWithDetails(id)
                .map(productDTOMapper::mapFull)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public ProductDTO addProduct(CreateProductRequestDTO request) {
        Product product = Product.builder()
                .name(request.name())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .category(request.category())
                .isSellable(false)
                .build();

        List<ProductDetail> detailList = request.details()
                .stream()
                .map(e -> ProductDetail
                        .builder()
                        .detailName(e.detailName())
                        .detail(e.detail())
                        .product(product)
                        .build())
                .toList();
        product.setProductDetails(detailList);
        Product savedProduct = productRepository.save(product);
        log.info("Product added to db : {}", savedProduct);
        return productDTOMapper.mapFull(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long productId, UpdateProductRequestDTO updateProductRequestDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<String> changed = new ArrayList<>();
        if (updateProductRequestDTO.name() != null && !updateProductRequestDTO.name().equals(product.getName())) {
            product.setName(updateProductRequestDTO.name());
            changed.add("name");
        }
        if (updateProductRequestDTO.price() != null && !updateProductRequestDTO.price().equals(product.getPrice())) {
            product.setPrice(updateProductRequestDTO.price());
            changed.add("price");
        }
        if (updateProductRequestDTO.category() != null && !updateProductRequestDTO.category().equals(product.getCategory())) {
            product.setCategory(updateProductRequestDTO.category());
            changed.add("category");
        }
        if (updateProductRequestDTO.imageUrl() != null && !updateProductRequestDTO.imageUrl().equals(product.getImageUrl())) {
            product.setImageUrl(updateProductRequestDTO.imageUrl());
            changed.add("imageUrl");
        }
        if (updateProductRequestDTO.sellable() != null && !Objects.equals(updateProductRequestDTO.sellable(), product.isSellable())) {
            product.setSellable(updateProductRequestDTO.sellable());
            changed.add("sellable");
        }
        if (changed.isEmpty()) {
            log.info("Product id {} update skipped. No changed were found", product.getId());
        } else {
            log.info(
                    "Product {} updated. Fields changed: {}",
                    product.getId(),
                    changed
            );
        }
        log.info(
                "Product updated. id={}, name={}, price={}, category={}",
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory()
        );
        return productDTOMapper.mapSimple(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        productRepository.delete(product);
    }

    @Transactional
    public Page<ProductDTO> searchProducts(ProductSearchDTO search, Pageable pageable) {

        Specification<Product> spec = Specification.allOf();
        if (search.name() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(
                            cb.lower(root.get("name")),
                            "%" + search.name().toLowerCase() + "%"
                    ));
        }
        if (search.category() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category"), search.category()));
        }
        if (search.sellable() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("sellable"), search.sellable()));
        }
        if (search.maxPrice() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), search.maxPrice()));
        }
        if (search.minPrice() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), search.minPrice()));
        }
        return productRepository.findAll(spec, pageable)
                .map(productDTOMapper::mapSimple);
    }

}
