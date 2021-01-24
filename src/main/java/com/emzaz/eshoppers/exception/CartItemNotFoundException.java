package com.emzaz.eshoppers.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String s) {
        super(s);
    }
}
