package com.sample.postgress.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.sample.postgress.dao.ProductRepository;
import com.sample.postgress.entity.Product;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void saveAllProducts_largeDataset_shouldHandleLargeVolume() {
        // Simuler 10 000 produits (limité pour les tests, ajustez pour 3M avec PostgreSQL)
        List<Product> products = generateLargeProductList(10_000);
        long startTime = System.currentTimeMillis();

        productService.saveAllProducts(products);

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Temps d'insertion pour 10 000 produits : " + duration + " ms");
        		  //        Temps d'insertion pour 10 000 produits : 815 ms
        assertEquals(10_000, productRepository.count());
    }
    
    @Test
    public void saveAllProductsJdbc() {
    	// Simuler 100 000 produits (limité pour les tests, ajustez pour 3M avec PostgreSQL)
        List<Product> products = generateLargeProductList(100_000);
        long startTime = System.currentTimeMillis();

        productService.saveAllProductsJdbc(products, 500);

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Temps d'insertion pour 100 000 produits : " + duration + " ms");
        assertEquals(100_000, productRepository.count());
    }

    private List<Product> generateLargeProductList(int size) {
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            Product p = new Product();
            p.setName("Product " + i);
            p.setDescription("Description for Product " + i);
            p.setPrice(new BigDecimal("99.99"));
            products.add(p);
        }
        return products;
    }
}