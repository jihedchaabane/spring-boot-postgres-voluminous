package com.sample.postgress.stepdefs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.sample.postgress.dao.ProductRepository;
import com.sample.postgress.entity.Product;
import com.sample.postgress.service.ProductService;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ProductStepDefs {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    private Response response;
    private List<Product> productsToSend;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        productRepository.deleteAll();
    }

    @Given("the product API is running")
    public void theProductApiIsRunning() {
        System.out.println("Product API is running");
    }

    @When("I send a POST request to {string} with the following data:")
    public void iSendAPostRequestWithData(String endpoint, List<Map<String, String>> dataTable) {
        Product product = new Product();
        Map<String, String> data = dataTable.get(0);
        product.setName(data.get("name"));
        product.setDescription(data.get("description"));
        product.setPrice(new BigDecimal(data.get("price")));

        response = given()
            .contentType(ContentType.JSON)
            .body(product)
            .when()
            .post(endpoint);
    }

    @When("I send a POST request to {string} with {int} products")
    public void iSendAPostRequestWithMultipleProducts(String endpoint, int count) {
        productsToSend = generateProducts(count);
        response = given()
            .contentType(ContentType.JSON)
            .body(productsToSend)
            .when()
            .post(endpoint);
    }

    @Given("there are {int} products in the database")
    public void thereAreProductsInTheDatabase(int count) {
        List<Product> products = generateProducts(count);
        productRepository.saveAll(products);
    }

    @Given("a product exists with ID {long} and name {string}")
    public void aProductExistsWithIdAndName(Long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("Description for " + name);
        product.setPrice(new BigDecimal("99.99"));
        product = productRepository.save(product);
        System.out.println(product);
    }

    @Given("a product exists with ID {long}")
    public void aProductExistsWithId(Long id) {
        aProductExistsWithIdAndName(id, "Test Product");
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequest(String endpoint) {
        response = given()
            .contentType(ContentType.JSON)
            .when()
            .get(endpoint);
    }

    @When("I send a DELETE request to {string}")
    public void iSendADeleteRequest(String endpoint) {
        response = given()
            .contentType(ContentType.JSON)
            .when()
            .delete(endpoint);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("the response should contain a product with name {string}")
    public void theResponseShouldContainProductWithName(String name) {
        response.then().body("name", equalTo(name));
    }

    @Then("the response should contain {int} products")
    public void theResponseShouldContainProducts(int count) {
        response.then().body("content.size()", equalTo(count));
    }

    @Then("the response should indicate a total of {int} products")
    public void theResponseShouldIndicateTotalProducts(int total) {
        response.then().body("totalElements", equalTo(total));
    }

    @Then("the product with ID {long} should no longer exist")
    public void theProductShouldNoLongerExist(Long id) {
        assertFalse(productRepository.findById(id).isPresent());
    }

    @When("I send a POST request to {string} with {int} products using JdbcTemplate")
    public void iSendAPostRequestWithMultipleProductsJdbc(String endpoint, int count) {
        productsToSend = generateProducts(count);
        
        long startTime = System.currentTimeMillis();
        productService.saveAllProductsJdbc(productsToSend, 500);
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Temps d'insertion pour " + count + " produits : " + duration + " ms");
        
        response = given()
            .contentType(ContentType.JSON)
            .body(productsToSend)
            .when()
            .post(endpoint);
    }

    private List<Product> generateProducts(int count) {
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Product product = new Product();
            product.setName("Product " + i);
            product.setDescription("Description for Product " + i);
            product.setPrice(new BigDecimal("99.99"));
            products.add(product);
        }
        return products;
    }
}