package com.emzaz.eshoppers.service;

import com.emzaz.eshoppers.dtos.Product;
import com.emzaz.eshoppers.dtos.ProductDto;

import java.util.List;

public interface ProductService {
    List<Product> findAllProductsSortedByName();
}
