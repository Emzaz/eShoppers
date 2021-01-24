package com.emzaz.eshoppers.service;

import com.emzaz.eshoppers.domain.Order;
import com.emzaz.eshoppers.domain.ShippingAddress;
import com.emzaz.eshoppers.dtos.Cart;
import com.emzaz.eshoppers.dtos.ShippingAddressDto;
import com.emzaz.eshoppers.dtos.UserDto;
import com.emzaz.eshoppers.exception.CartItemNotFoundException;
import com.emzaz.eshoppers.repository.CartRepository;
import com.emzaz.eshoppers.repository.OrderRepository;
import com.emzaz.eshoppers.repository.ShippingAddressRepository;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.Optional;

public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;
    private ShippingAddressRepository shippingAddressRepository;
    private CartRepository cartRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ShippingAddressRepository shippingAddressRepository,
                            CartRepository cartRepository) {

        this.orderRepository = orderRepository;
        this.shippingAddressRepository = shippingAddressRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public void processOrder(ShippingAddressDto shippingAddressDto, UserDto currentUser) throws CartItemNotFoundException {
        ShippingAddress shippingAddress =  convertTo(shippingAddressDto);

        ShippingAddress saveShippingAddress = shippingAddressRepository.save(shippingAddress);

        Optional<Cart> cartOptional = cartRepository.findByUser(currentUser);
        Cart cart;
        if(cartOptional.isPresent()) {
            cart = cartOptional.get();
        } else {
            throw new CartItemNotFoundException("Cart not found by current user");
        }

        Order order = new Order();
        order.setCart(cart);
        order.setShippingAddress(saveShippingAddress);
        order.setShipped(false);
        order.setUserDto(currentUser);

        orderRepository.save(order);
    }

    private ShippingAddress convertTo(ShippingAddressDto shippingAddressDto) {
        ShippingAddress shippingAddress = new ShippingAddress();

        shippingAddress.setAddress(shippingAddressDto.getAddress());
        shippingAddress.setAddress2(shippingAddressDto.getAddress2());
        shippingAddress.setCountry(shippingAddressDto.getCountry());
        shippingAddress.setMobileNumber(shippingAddressDto.getMobileNumber());
        shippingAddress.setState(shippingAddressDto.getState());
        shippingAddress.setZip(shippingAddressDto.getZip());

        return shippingAddress;
    }
}
