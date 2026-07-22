package com.kelaryon.store_management_tool.admin;


import com.kelaryon.store_management_tool.data.*;
import com.kelaryon.store_management_tool.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;

    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO addProduct(
            @Valid @RequestBody CreateProductRequestDTO request) {
        return productService.addProduct(request);
    }

    @PatchMapping("/products/{id}")
    public ProductDTO updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequestDTO request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/products")
    public Page<ProductDTO> searchProducts(
            ProductSearchDTO searchParams,
            @PageableDefault(size = 20) Pageable pageable) {

        return productService.searchProducts(searchParams, pageable);
    }
}
