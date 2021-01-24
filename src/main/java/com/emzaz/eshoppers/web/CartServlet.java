package com.emzaz.eshoppers.web;

import com.emzaz.eshoppers.dtos.Cart;
import com.emzaz.eshoppers.dtos.UserDto;
import com.emzaz.eshoppers.exception.ProductNotFoundException;
import com.emzaz.eshoppers.repository.*;
import com.emzaz.eshoppers.service.CartService;
import com.emzaz.eshoppers.service.CartServiceImpl;
import com.emzaz.eshoppers.util.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CartServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(CartServlet.class);

    private final CartService cartService = new CartServiceImpl(new JdbcCartRepositoryImpl(),
            new JdbcProductRepositoryImpl(),
            new JdbcCartItemRepositoryImpl());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, ProductNotFoundException {
        String productId = req.getParameter("productId");

        LOGGER.info("Received request to add Product with id: {} to cart", productId);

        Cart cart = getCart(req);

        cartService.addProductToCart(productId, cart);

        resp.sendRedirect("/home");
    }

    private Cart getCart(HttpServletRequest req) {
        final UserDto currentUser = SecurityContext.getCurrentUser(req);

        return cartService.getCartByUser(currentUser);
    }
}
