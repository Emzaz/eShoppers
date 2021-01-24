package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.dtos.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryImpl implements ProductRepository {
    private static final List<Product> ALL_PRODUCTS = Arrays.asList(
            new Product(
                    "Apple iPad",
                    "Apple iPad 10.2 32GB",
                    BigDecimal.valueOf(369.99)
            ),
            new Product(
                    "Headphones",
                    "Jabra Elite Bluetooth Headphones",
                    BigDecimal.valueOf(249.99)
            ),
            new Product(
                    "Microsoft Surface Pro",
                    "Microsoft Surface Pro 7 12.3",
                    BigDecimal.valueOf(799.99)
            ),
            new Product(
                    "Amazon Echo Dot",
                    "Amazon Echo Dot (3rd gen) with Alexa - charcol",
                    BigDecimal.valueOf(34.99)
            )
    );

    @Override
    public List<Product> allProducts() {
        return ALL_PRODUCTS;
    }

    @Override
    public Optional<Product> findById(Long productId) {
        for(Product product : ALL_PRODUCTS) {
            if(product.getId().equals(productId)) {
               return Optional.of(product);
            }
        }
        return Optional.empty();
    }
}
