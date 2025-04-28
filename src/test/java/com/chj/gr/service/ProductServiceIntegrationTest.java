package com.chj.gr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.chj.gr.model.Product;
import com.chj.gr.repository.ProductRepository;

/**
 * mvn test -Dtest=ProductServiceIntegrationTest
 */

@SpringBootTest
@ActiveProfiles("postgres")
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
        // Simuler $count produits (limité pour les tests, ajustez pour 3M avec PostgreSQL)
    	int count = 1000_000;
        List<Product> products = generateLargeProductList(count);
       
        long startTime = System.currentTimeMillis();
        productService.saveAllProducts(products);
        
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Temps d'insertion pour " + count + " produits : " + duration + " ms");
        		    //      Temps d'insertion pour    10 000 produits :   5 796 ms
        			//		Temps d'insertion pour   100 000 produits :  48 715 ms 		almost 1 minute
        			//		Temps d'insertion pour 1 000 000 produits : 479 203 ms 		almost 8 minutes.
        assertEquals(count, productRepository.count());
    }
    
    @Test
    public void saveAllProductsJdbc() {
    	// Simuler $count produits (limité pour les tests, ajustez pour 3M avec PostgreSQL)
    	int count = 5000_000;
        List<Product> products = generateLargeProductList(count);
        
        long startTime = System.currentTimeMillis();
        productService.saveAllProductsJdbc(products, 500);
        long duration = System.currentTimeMillis() - startTime;
        
        System.out.println("Temps d'insertion pour " + count + " produits : " + duration + " ms");
					//		Temps d'insertion pour    10 000 produits :     377 ms
        			//		Temps d'insertion pour   100 000 produits :   2 436 ms
        			//		Temps d'insertion pour 1 000 000 produits :  20 889 ms
        			//		Temps d'insertion pour 5 000 000 produits : 112 502 ms		almost 1.9 minutes
        assertEquals(count, productRepository.count());
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