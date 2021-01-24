package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.dtos.Cart;
import com.emzaz.eshoppers.dtos.UserDto;

import java.util.Optional;

public interface CartRepository {
    Optional<Cart> findByUser(UserDto currentUser);

    Cart save(Cart cart);

    Cart update(Cart cart);

    Optional<Cart> findOne(long cartId);
}
