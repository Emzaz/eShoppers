package com.emzaz.eshoppers.service;

import com.emzaz.eshoppers.dtos.ShippingAddressDto;
import com.emzaz.eshoppers.dtos.UserDto;

public interface OrderService {
    void processOrder(ShippingAddressDto shippingAddressDto, UserDto currentUser);
}
