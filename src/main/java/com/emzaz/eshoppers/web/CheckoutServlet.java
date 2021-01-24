package com.emzaz.eshoppers.web;

import com.emzaz.eshoppers.dtos.Cart;
import com.emzaz.eshoppers.dtos.UserDto;
import com.emzaz.eshoppers.repository.*;
import com.emzaz.eshoppers.service.CartService;
import com.emzaz.eshoppers.service.CartServiceImpl;
import com.emzaz.eshoppers.util.SecurityContext;
import com.emzaz.eshoppers.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CheckoutServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutServlet.class);

    private CartService cartService = new CartServiceImpl
            (new JdbcCartRepositoryImpl(),
            new JdbcProductRepositoryImpl(),
            new JdbcCartItemRepositoryImpl());

    public enum Action {
        ADD,
        SUB,
        REMOVE;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Serving checkout page");

        UserDto currentUser = SecurityContext.getCurrentUser(req);
        Cart cart = cartService.getCartByUser(currentUser);

        req.setAttribute("cart", cart);

        req.getRequestDispatcher("/WEB-INF/checkout.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String productId = req.getParameter("productId");
        String action = req.getParameter("action");
        Cart cart = getCart(req);

        if(StringUtil.isNotEmpty(action)) {
            processCart(productId, action, cart);

            resp.sendRedirect("/checkout");
            return;
        }

        LOGGER.info("Received request to add product with id: {} to cart", productId);

        cartService.addProductToCart(productId, cart);
        resp.sendRedirect("/home");
    }

    private Cart getCart(HttpServletRequest req) {
        final UserDto currentUser = SecurityContext.getCurrentUser(req);

        return cartService.getCartByUser(currentUser);
    }

    private void processCart(String productId, String action, Cart cart) {
        switch (Action.valueOf(action.toUpperCase())) {
            case ADD:
                LOGGER.info("Received request to add product with id: {} to cart", productId);

                cartService.addProductToCart(productId, cart);
                break;

            case SUB:
                LOGGER.info("Received request to subtract product with id: {} to cart", productId);

                cartService.subProductToCart(productId, cart);
                break;

            case REMOVE:
                LOGGER.info("Received request to remove product with id: {} to cart", productId);

                cartService.removeProductToCart(productId, cart);
                break;
        }
    }
}
