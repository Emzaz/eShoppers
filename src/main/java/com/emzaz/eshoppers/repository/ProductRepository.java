package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.dtos.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> allProducts();

    Optional<Product> findById(Long productId);
}
