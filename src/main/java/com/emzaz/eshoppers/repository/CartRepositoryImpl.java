package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.domain.Order;
import com.emzaz.eshoppers.dtos.Cart;
import com.emzaz.eshoppers.dtos.UserDto;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CartRepositoryImpl implements CartRepository {
    private static final Map<UserDto, Set<Cart>> CARTS = new ConcurrentHashMap<>();

    private OrderRepository orderRepository = new OrderRepositoryImpl();

    @Override
    public Optional<Cart> findOne(long cartId) {
        return Optional.empty();
    }

    @Override
    public Optional<Cart> findByUser(UserDto currentUser) {
        Optional<Cart> usersCart = getCart(currentUser);

        if(usersCart.isPresent()) {
            Cart cart = usersCart.get();
            Set<Order> orders = orderRepository.findOrderByUser(currentUser);

            if(isOrderAlreadyPlacedWith(orders, cart)) {
                return Optional.empty();
            } else {
                return Optional.of(cart);
            }
        }

        return Optional.empty();
    }

    private boolean isOrderAlreadyPlacedWith(Set<Order> orders, Cart cart) {
        for(Order order : orders) {
            if(order.getCart().equals(cart)) {
                return true;
            }
        }
        return false;
    }

    private Optional<Cart> getCart(UserDto currentUser) {
        Set<Cart> carts = CARTS.get(currentUser);
        if(carts != null && !carts.isEmpty()) {
            Cart cart = (Cart) carts.toArray()[carts.size() - 1];
            return Optional.of(cart);
        }

        return Optional.empty();
    }

    @Override
    public Cart save(Cart cart) {
        Set<Cart> carts = CARTS.get(cart.getUserDto());

        if(carts != null) {
            carts.add(cart);
            CARTS.put(cart.getUserDto(), carts);
        } else {
            Set<Cart> newCartSet = new LinkedHashSet<Cart>();
            newCartSet.add(cart);
            CARTS.put(cart.getUserDto(), newCartSet);
        }

        return cart;
    }

    @Override
    public Cart update(Cart cart) {
        CARTS.computeIfPresent(cart.getUserDto(), (userDTO, carts) -> {
            Cart[] objects = carts.stream().toArray(Cart[]::new);

            objects[objects.length - 1] = cart;

            return new LinkedHashSet<Cart>(Arrays.asList(objects));
        });

        return cart;
    }
}
