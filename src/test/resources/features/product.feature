Feature: Product Management
  Product Management As a user, I want to manage products via REST API
  So that I can create, retrieve, and delete products, including in large volumes

  Background:
    Given the product API is running

  Scenario: Create a single product
    When I send a POST request to "/api/products" with the following data:
      | name        | description        | price |
      | Test Product | Product description | 99.99 |
    Then the response status should be 200
    And the response should contain a product with name "Test Product"

  Scenario: Create multiple products in batch
    When I send a POST request to "/api/products/batch" with 100 products
    Then the response status should be 200
    And the response should contain 100 products

  Scenario: Retrieve all products with pagination
    Given there are 150 products in the database
    When I send a GET request to "/api/products?page=0&size=100"
    Then the response status should be 200
    And the response should contain 100 products
    And the response should indicate a total of 150 products

  Scenario: Retrieve a product by ID
    Given a product exists with ID 1 and name "Test Product"
    When I send a GET request to "/api/products/1"
    Then the response status should be 200
    And the response should contain a product with name "Test Product"

  Scenario: Delete a product
    Given a product exists with ID 1
    When I send a DELETE request to "/api/products/1"
    Then the response status should be 204
    And the product with ID 1 should no longer exist

  Scenario: Create a large number of products in batch
    When I send a POST request to "/api/products/batch" with 1000 products
    Then the response status should be 200
    And the response should contain 1000 products