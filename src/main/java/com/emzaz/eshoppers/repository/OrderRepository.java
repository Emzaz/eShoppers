package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.domain.Order;
import com.emzaz.eshoppers.dtos.UserDto;

import java.util.Set;

public interface OrderRepository {
    Order save(Order order);
    Set<Order> findOrderByUser(UserDto currentUser);
}
