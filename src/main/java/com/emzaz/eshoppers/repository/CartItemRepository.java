package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.dtos.CartItem;

public interface CartItemRepository {
    CartItem save(CartItem cartItem);

    CartItem update(CartItem cartItem);

    void remove(CartItem cartItem);
}
