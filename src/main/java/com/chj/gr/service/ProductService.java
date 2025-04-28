package com.chj.gr.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chj.gr.model.Product;
import com.chj.gr.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final JdbcTemplate jdbcTemplate;

    public ProductService(ProductRepository productRepository, JdbcTemplate jdbcTemplate) {
        this.productRepository = productRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public List<Product> saveAllProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }

    @Transactional
    public void saveAllProductsJdbc(List<Product> products, int batchSize) {
        String sql = "INSERT INTO product (name, description, price) VALUES (?, ?, ?)";
//        int currentBatch = 1;
        for (int i = 0; i < products.size(); i += batchSize) {
            List<Product> batch = products.subList(i, Math.min(i + batchSize, products.size()));
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int idx) throws SQLException {
                    Product p = batch.get(idx);
                    ps.setString(1, p.getName());
                    ps.setString(2, p.getDescription());
                    ps.setBigDecimal(3, p.getPrice());
                }

                @Override
                public int getBatchSize() {
                    return batch.size();
                }
            });
//            System.out.println("Batch " + currentBatch + " : Saved " + (500 * currentBatch) + " products.");
//            currentBatch ++;
        }
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}