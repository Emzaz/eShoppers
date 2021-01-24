package com.emzaz.eshoppers.dtos;

import com.emzaz.eshoppers.domain.Domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Cart extends Domain {
    private Set<CartItem> cartItems = new HashSet<>();
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private Integer totalItem = 0;
    private UserDto userDto;

    public Cart() {
    }

    public Cart(Set<CartItem> cartItems, BigDecimal totalPrice, Integer totalItem, UserDto userDto) {
        this.cartItems = cartItems;
        this.totalPrice = totalPrice;
        this.totalItem = totalItem;
        this.userDto = userDto;
    }

    public Set<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Set<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(Integer totalItem) {
        this.totalItem = totalItem;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }
}
