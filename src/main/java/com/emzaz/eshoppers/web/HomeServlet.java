package com.emzaz.eshoppers.web;

import com.emzaz.eshoppers.dtos.Cart;
import com.emzaz.eshoppers.dtos.Product;
import com.emzaz.eshoppers.dtos.UserDto;
import com.emzaz.eshoppers.repository.*;
import com.emzaz.eshoppers.service.CartService;
import com.emzaz.eshoppers.service.CartServiceImpl;
import com.emzaz.eshoppers.service.ProductService;
import com.emzaz.eshoppers.service.ProductServiceImpl;
import com.emzaz.eshoppers.dtos.ProductDto;
import com.emzaz.eshoppers.util.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class HomeServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeServlet.class);

    private ProductService productService = new ProductServiceImpl(new JdbcProductRepositoryImpl());

    private CartService cartService = new CartServiceImpl(new JdbcCartRepositoryImpl(),
            new JdbcProductRepositoryImpl(),
            new JdbcCartItemRepositoryImpl());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Serving Home Page");

        final String attribute = req.getParameter("orderSuccess");
        if(attribute != null && Boolean.parseBoolean(attribute)) {
            req.setAttribute("message",
                    "<strong>Congratulation! </strong> You're order has been placed successfully. ");
        }

        List<Product> productList = productService.findAllProductsSortedByName();

        LOGGER.info("Total product found {}", productList.size());

        Cart cart = null;

        if(SecurityContext.isAuthenticated(req)) {
            UserDto currentUser = SecurityContext.getCurrentUser(req);
            cart = cartService.getCartByUser(currentUser);
            req.setAttribute("cart", cart);
        } else {
            req.setAttribute("cart", cart);
        }

        req.setAttribute("products", productList);
        req.getRequestDispatcher("/WEB-INF/home.jsp")
                .forward(req, resp);
    }
}
