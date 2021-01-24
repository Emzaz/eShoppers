package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.dtos.Cart;
import com.emzaz.eshoppers.dtos.CartItem;
import com.emzaz.eshoppers.dtos.UserDto;
import com.emzaz.eshoppers.exception.CartNotFoundException;
import com.emzaz.eshoppers.exception.OptimisticLockingFailureException;
import com.emzaz.eshoppers.jdbc.JDBCTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class JdbcCartRepositoryImpl implements CartRepository {
    private JDBCTemplate jdbcTemplate = new JDBCTemplate();
    private ProductRepository productRepository = new JdbcProductRepositoryImpl();

    private static final String INSERT_CART = "INSERT INTO cart(" +
            "total_price, " +
            "total_item, " +
            "version, " +
            "date_created, " +
            "date_last_updated, " +
            "user_id) " +
            "VALUES(?, ?, ?, ?, ?, ?)";

    private static final String FIND_BY_USER = "SELECT c.* " +
            "FROM cart c " +
            "INNER JOIN user u on (c.user_id = u.id) " +
            "WHERE u.id = ? " +
            "ORDER BY c.id desc LIMIT 1";

    private static final String FIND_BY_ID = "SELECT id, " +
            "total_price, " +
            "total_item, " +
            "version, " +
            "date_created, " +
            "date_last_updated, " +
            "user_id " +
            "FROM cart " +
            "WHERE id = ? ";

    private static final String FIND_ALL_CART_ITEMS = "SELECT *" +
            "from cart_item c " +
            "where c.cart_id = ? ";

    private static final String UPDATE_CART = "UPDATE cart " +
            "SET total_price = ? " +
            ", total_item = ? " +
            ", version = ? " +
            ", date_last_updated = ? WHERE id = ? ";

    @Override
    public Optional<Cart> findByUser(UserDto currentUser) {
        List<Cart> carts = jdbcTemplate.queryForObject(FIND_BY_USER,
                currentUser.getId(),
                resultSet -> {
            Cart cart = extractCart(resultSet);
                    List<CartItem> allCartItems = findAllCartItems(cart.getId());
                    cart.getCartItems().addAll(allCartItems);

                    return cart;
                });

        return carts.size() > 0
                ? Optional.of((carts.get(0)))
                : Optional.empty();
    }

    private List<CartItem> findAllCartItems(Long id) {
        return jdbcTemplate.queryForObject(FIND_ALL_CART_ITEMS,
                id, resultSet -> {
            CartItem cartItem = new CartItem();
            cartItem.setId(resultSet.getLong("id"));
            cartItem.setQuantity(resultSet.getInt("quantity"));
            cartItem.setPrice(resultSet.getBigDecimal("price"));
            cartItem.setVersion(resultSet.getLong("version"));
            cartItem.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());
            cartItem.setDateLastUpdated(resultSet.getTimestamp("date_last_updated").toLocalDateTime());

            Long productId = resultSet.getLong("product_id");
            productRepository.findById(productId).ifPresent(cartItem :: setProduct);

            return cartItem;
                });
    }

    @Override
    public Cart save(Cart cart) {
        Long id = jdbcTemplate.executeInsertQuery(INSERT_CART,
                cart.getTotalPrice(),
                cart.getTotalItem(),
                0L,
                cart.getDateCreated(),
                cart.getDateLastUpdated(),
                cart.getUserDto().getId());

        cart.setId(id);

        return cart;
    }

    @Override
    public Cart update(Cart cart) {
        cart.setVersion(cart.getVersion() + 1);

        Cart cartToUpdate = findOne(cart.getId()).orElseThrow(() ->
                new CartNotFoundException("Shopping cart not found by id " + cart.getId()));

        if(cart.getVersion() <= (cartToUpdate.getVersion())) {
            throw new OptimisticLockingFailureException("Shopping cart is already updated by another request");
        }

        cartToUpdate.setTotalPrice(cart.getTotalPrice());
        cartToUpdate.setTotalItem(cart.getTotalItem());
        cartToUpdate.setVersion(cart.getVersion());
        cartToUpdate.setDateLastUpdated(LocalDateTime.now());
        cartToUpdate.getCartItems().addAll(cart.getCartItems());

        jdbcTemplate.updateQuery(UPDATE_CART,
                cartToUpdate.getTotalPrice(),
                cartToUpdate.getTotalItem(),
                cartToUpdate.getVersion(),
                cartToUpdate.getDateLastUpdated(),
                cartToUpdate.getId());

        return cartToUpdate;
    }

    @Override
    public Optional<Cart> findOne(long cartId) {
        List<Cart> carts = jdbcTemplate.queryForObject(FIND_BY_ID,
                cartId,
                this::extractCart);

        return carts.size() > 0
                ? Optional.of(carts.get(0))
                : Optional.empty();
    }

    private Cart extractCart(ResultSet resultSet) throws SQLException {
        Cart cart = new Cart();

        cart.setId(resultSet.getLong("id"));
        cart.setTotalPrice(resultSet.getBigDecimal("total_price"));
        cart.setTotalItem(resultSet.getInt("total_item"));
        cart.setVersion(resultSet.getLong("version"));
        cart.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());
        cart.setDateLastUpdated(resultSet.getTimestamp("date_last_updated").toLocalDateTime());

        return cart;
    }
}
