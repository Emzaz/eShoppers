package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.domain.ShippingAddress;

import java.util.Optional;

public interface ShippingAddressRepository {
    ShippingAddress save(ShippingAddress convertTO);

    Optional<ShippingAddress> findOne(long shippingAddressId);
}
