package com.sample.postgress.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.postgress.entity.Product;
import com.sample.postgress.service.ProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Description");
        product.setPrice(new BigDecimal("99.99"));
    }

    @Test
    void createProduct_shouldReturnCreatedProduct() throws Exception {
        when(productService.saveProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).saveProduct(any(Product.class));
    }

    @Test
    void createProductsBatch_shouldHandleLargeBatch() throws Exception {
        List<Product> products = generateLargeProductList(100);
        when(productService.saveAllProducts(anyList())).thenReturn(products);

        mockMvc.perform(post("/api/products/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(products)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(100));

        verify(productService, times(1)).saveAllProducts(anyList());
    }

//    @Test
//    void getAllProducts_shouldReturnProductList() throws Exception {
//        List<Product> products = Arrays.asList(product);
//        Pageable pageable = PageRequest.of(0, 100);
//        when(productService.getAllProducts(pageable)).thenReturn(products);
//
//        mockMvc.perform(get("/api/products")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(1))
//                .andExpect(jsonPath("$[0].name").value("Test Product"));
//
//        verify(productService, times(1)).getAllProducts();
//    }

    @Test
    void getProductById_shouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void deleteProduct_shouldReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    private List<Product> generateLargeProductList(int size) {
        Product[] products = new Product[size];
        for (int i = 0; i < size; i++) {
            Product p = new Product();
            p.setId((long) (i + 1));
            p.setName("Product " + (i + 1));
            p.setDescription("Description for Product " + (i + 1));
            p.setPrice(new BigDecimal("99.99"));
            products[i] = p;
        }
        return Arrays.asList(products);
    }
}
