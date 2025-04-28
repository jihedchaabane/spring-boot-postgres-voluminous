package com.chj.gr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chj.gr.model.Product;
import com.chj.gr.repository.ProductRepository;
import com.chj.gr.service.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

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
    void saveProduct_shouldSaveAndReturnProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = productService.saveProduct(product);

        assertNotNull(savedProduct);
        assertEquals("Test Product", savedProduct.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void saveAllProducts_shouldSaveMultipleProducts() {
        List<Product> products = Arrays.asList(
                createProduct(1L, "Product 1"),
                createProduct(2L, "Product 2"),
                createProduct(3L, "Product 3")
        );
        when(productRepository.saveAll(anyList())).thenReturn(products);

        List<Product> savedProducts = productService.saveAllProducts(products);

        assertEquals(3, savedProducts.size());
        assertEquals("Product 1", savedProducts.get(0).getName());
        verify(productRepository, times(1)).saveAll(products);
    }

    @Test
    void saveAllProducts_largeDataset_shouldHandleLargeBatch() {
        // Simuler un grand ensemble de données (1000 produits)
        List<Product> largeProductList = generateLargeProductList(1000);
        when(productRepository.saveAll(anyList())).thenReturn(largeProductList);

        List<Product> savedProducts = productService.saveAllProducts(largeProductList);

        assertEquals(1000, savedProducts.size());
        verify(productRepository, times(1)).saveAll(largeProductList);
    }

//    @Test
//    void getAllProducts_shouldReturnAllProducts() {
//        List<Product> products = Arrays.asList(product);
//        when(productRepository.findAll()).thenReturn(products);
//
//        Pageable pageable = PageRequest.of(0, 100);
//        Page<Product> result = productService.getAllProducts(pageable);
//
//        assertEquals(1, result.getSize());
//        assertEquals("Test Product", result.stream().findFirst().get().getName());
//        verify(productRepository, times(1)).findAll();
//    }

    @Test
    void getProductById_shouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_shouldThrowExceptionWhenNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getProductById(1L);
        });

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void deleteProduct_shouldDeleteProduct() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    private Product createProduct(Long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("Description for " + name);
        product.setPrice(new BigDecimal("99.99"));
        return product;
    }

    private List<Product> generateLargeProductList(int size) {
        Product[] products = new Product[size];
        for (int i = 0; i < size; i++) {
            products[i] = createProduct((long) (i + 1), "Product " + (i + 1));
        }
        return Arrays.asList(products);
    }
}