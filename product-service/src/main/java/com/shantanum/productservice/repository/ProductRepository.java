package com.shantanum.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shantanum.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}