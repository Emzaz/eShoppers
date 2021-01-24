package com.emzaz.eshoppers.service;

import com.emzaz.eshoppers.dtos.Product;
import com.emzaz.eshoppers.dtos.ProductDto;
import com.emzaz.eshoppers.repository.JdbcProductRepositoryImpl;
import com.emzaz.eshoppers.repository.ProductRepository;
import com.emzaz.eshoppers.repository.ProductRepositoryImpl;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;

    public ProductServiceImpl() {
    }

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public List<Product> findAllProductsSortedByName() {
        List<Product> productList = productRepository.allProducts();

        productList.sort((Product p1,Product p2) -> p1.getName().compareTo(p2.getName()));

        return productList;
    }

    private ProductDto convertToDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
