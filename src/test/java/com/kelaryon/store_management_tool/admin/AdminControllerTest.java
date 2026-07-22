package com.kelaryon.store_management_tool.admin;

import com.kelaryon.store_management_tool.data.Product;
import com.kelaryon.store_management_tool.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;
    private final String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzg0NzI2MzEyLCJleHAiOjE3ODUzMzExMTJ9.NXh8-5FBhVjjXe5GWv5d1wK_CAEFNg_MdSjODamZbAU";

    @BeforeEach
    void cleanDb() {
        productRepository.deleteAll();
    }

    @Test
    void addProduct() throws Exception {
        mockMvc.perform(post("/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content("""
                                    {
                                        "name":"Prod_name_test",
                                        "price":"10.5",
                                        "category":"test_category",
                                        "details": [{
                                            "detailName":"detail_test_1",
                                            "detail":"detail_test_1_data"
                                        },
                                        {
                                            "detailName":"detail_test_2",
                                            "detail":"detail_test_2_data"
                                        }]
                                    }
                                """))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void getProducts() throws Exception {
        createProduct("Prod_test_1", 15.2, "flex");
        createProduct("Prod_test_2", 51.1, "mango");
        mockMvc.perform(get("/admin/products")
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Prod_test_1"))
                .andExpect(jsonPath("$.content[0].price").value(15.2))
                .andExpect(jsonPath("$.content[0].category").value("flex"))
                .andExpect(jsonPath("$.content[1].name").value("Prod_test_2"))
                .andExpect(jsonPath("$.content[1].price").value(51.1))
                .andExpect(jsonPath("$.content[1].category").value("mango"));
    }

    @Test
    void getProductById() throws Exception {

        Product product = createProduct();

        mockMvc.perform(get("/admin/products/" + product.getId())
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(product.getId()));
    }

    @Test
    void updateProduct() throws Exception {

        Product product = createProduct();

        mockMvc.perform(patch("/admin/products/" + product.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "name":"Updated Product",
                                        "price":20.0
                                    }
                                """))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Updated Product"));
    }


    @Test
    void deleteProduct() throws Exception {

        Product product = createProduct();
        Assertions.assertTrue(productRepository.existsById(product.getId()));
        mockMvc.perform(delete("/admin/products/" + product.getId())
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNoContent());
        Assertions.assertFalse(productRepository.existsById(product.getId()));
    }


    @Test
    void searchProducts() throws Exception {

        createProduct("Prod_test_1", 15.2, "flex");
        createProduct("Prod_test_2", 51.1, "mango");
        mockMvc.perform(get("/admin/products")
                        .param("category", "mango")
                        .header("Authorization", token))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }


    private Product createProduct() {
        return createProduct("Prod_test", 10.50, "test");
    }

    private Product createProduct(String name, Double price, String category) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        product.setCategory(category);

        return productRepository.save(product);
    }
}