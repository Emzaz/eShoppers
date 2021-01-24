package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.domain.Order;
import com.emzaz.eshoppers.dtos.UserDto;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class OrderRepositoryImpl implements OrderRepository {
    private static final Set<Order> ORDERS = new CopyOnWriteArraySet<>();

    @Override
    public Order save(Order order) {
        ORDERS.add(order);

        return order;
    }

    @Override
    public Set<Order> findOrderByUser(UserDto currentUser) {
        Set<Order> orders = new HashSet<>();
        for(Order order : ORDERS) {
            if(order.getUserDto().equals(currentUser)) {
                orders.add(order);
            }
        }
        return orders;
    }
}
