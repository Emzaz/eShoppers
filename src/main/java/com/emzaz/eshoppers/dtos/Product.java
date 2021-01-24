package com.emzaz.eshoppers.dtos;

import com.emzaz.eshoppers.domain.Domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Product extends Domain {
    private String name;
    private String description;
    private BigDecimal price;

    public Product() {
    }

    public Product(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
            if (!(obj instanceof Product)) return false;
            Product product = (Product) obj;
            return Objects.equals(getId(), product.getId());
    }
}
