package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.dtos.CartItem;
import com.emzaz.eshoppers.exception.CartItemNotFoundException;
import com.emzaz.eshoppers.exception.OptimisticLockingFailureException;
import com.emzaz.eshoppers.jdbc.ConnectionPool;
import com.emzaz.eshoppers.jdbc.JDBCTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCartItemRepositoryImpl implements CartItemRepository {
    private JDBCTemplate jdbcTemplate = new JDBCTemplate();
    private ProductRepository productRepository = new JdbcProductRepositoryImpl();

    private static final String INSERT_CART_ITEM = "INSERT INTO cart_item (" +
            "quantity, " +
            "price, " +
            "product_id, " +
            "version, " +
            "date_created, " +
            "date_last_updated, " +
            "cart_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_CART_ITEM = "UPDATE cart_item " +
            "SET quantity = ?, " +
            "price = ?, " +
            "version = ?, " +
            "date_last_updated = ? WHERE id = ? ";

    private static final String SELECT_CART_ITEM = "select id, " +
            "quantity, " +
            "price, " +
            "product_id, " +
            "version, " +
            "date_created, " +
            "date_last_updated, " +
            "cart_id " +
            "from cart_item " +
            "where id = ? ";

    private static final String DELETE_CART = "DELETE FROM cart_item WHERE id = ?";

    @Override
    public CartItem save(CartItem cartItem) {
        long cartItemId = jdbcTemplate.executeInsertQuery(INSERT_CART_ITEM,
                cartItem.getQuantity(),
                cartItem.getPrice(),
                cartItem.getProduct().getId(),
                0L,
                cartItem.getDateCreated(),
                cartItem.getDateLastUpdated(),
                cartItem.getCart().getId());
        cartItem.setId(cartItemId);

        return cartItem;
    }

    @Override
    public CartItem update(CartItem cartItem) {
        cartItem.setVersion(cartItem.getVersion() + 1);

        CartItem cartItemToUpdate = findOne(cartItem.getId())
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found by id, +" + cartItem.getId()));

        if(cartItem.getVersion() <= (cartItemToUpdate.getVersion())) {
            throw new OptimisticLockingFailureException("CarItem is already updated by another request");
        }

        cartItemToUpdate.setDateLastUpdated(LocalDateTime.now());
        cartItemToUpdate.setVersion(cartItem.getVersion());
        cartItemToUpdate.setProduct(cartItem.getProduct());
        cartItemToUpdate.setQuantity(cartItem.getQuantity());
        cartItemToUpdate.setPrice(cartItem.getPrice());

        jdbcTemplate.updateQuery(UPDATE_CART_ITEM,
                cartItemToUpdate.getQuantity(),
                cartItemToUpdate.getPrice(),
                cartItemToUpdate.getVersion(),
                cartItemToUpdate.getDateLastUpdated(),
                cartItemToUpdate.getId());

        return cartItemToUpdate;
    }

    private Optional<CartItem> findOne(Long id) {
        List<CartItem> cartItems = jdbcTemplate.queryForObject(SELECT_CART_ITEM,
                id, resultSet -> {
            CartItem cartItem = new CartItem();

            cartItem.setId(resultSet.getLong("id"));
            cartItem.setQuantity(resultSet.getInt("quantity"));
            cartItem.setPrice(resultSet.getBigDecimal("price"));
            cartItem.setVersion(resultSet.getLong("version"));
            cartItem.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());
            cartItem.setDateLastUpdated(resultSet.getTimestamp("date_last_updated").toLocalDateTime());

            Long productId = resultSet.getLong("product_id");

            productRepository.findById(productId).ifPresent(cartItem::setProduct);

            return cartItem;
                });

        return cartItems.size() > 0?
                Optional.of(cartItems.get(0)) : Optional.empty();
    }

    @Override
    public void remove(CartItem cartItem) {

        jdbcTemplate.deleteById(DELETE_CART, cartItem.getId());
    }
}
