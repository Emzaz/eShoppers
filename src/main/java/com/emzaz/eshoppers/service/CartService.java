package com.emzaz.eshoppers.service;

import com.emzaz.eshoppers.dtos.Cart;
import com.emzaz.eshoppers.dtos.UserDto;
import com.emzaz.eshoppers.exception.ProductNotFoundException;

public interface CartService {
    Cart getCartByUser(UserDto currentUser);

    void addProductToCart (String productId, Cart cart) throws ProductNotFoundException;

    void subProductToCart(String productId, Cart cart);

    void removeProductToCart(String productId, Cart cart);
}
