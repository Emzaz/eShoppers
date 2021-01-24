package com.emzaz.eshoppers.dtos;

import com.emzaz.eshoppers.domain.Domain;

import java.math.BigDecimal;
import java.util.Objects;

public class CartItem extends Domain {
    private Product product;
    private Integer quantity;
    private BigDecimal price;
    private Cart cart;

    public CartItem() {
    }

    public CartItem(Cart cart, Product product, Integer quantity, BigDecimal price) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
        if (!(obj instanceof CartItem)) return false;
        CartItem cartItem = (CartItem) obj;
        return Objects.equals(getId(), cartItem.getId());
    }
}
