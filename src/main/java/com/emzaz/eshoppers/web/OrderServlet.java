package com.emzaz.eshoppers.web;

import com.emzaz.eshoppers.domain.ShippingAddress;
import com.emzaz.eshoppers.dtos.Cart;
import com.emzaz.eshoppers.dtos.ShippingAddressDto;
import com.emzaz.eshoppers.dtos.UserDto;
import com.emzaz.eshoppers.repository.*;
import com.emzaz.eshoppers.service.CartService;
import com.emzaz.eshoppers.service.CartServiceImpl;
import com.emzaz.eshoppers.service.OrderService;
import com.emzaz.eshoppers.service.OrderServiceImpl;
import com.emzaz.eshoppers.util.SecurityContext;
import com.emzaz.eshoppers.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OrderServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServlet.class);

    private CartService cartService = new CartServiceImpl(
            new JdbcCartRepositoryImpl(),
                new JdbcProductRepositoryImpl(),
                    new JdbcCartItemRepositoryImpl());

    private OrderService orderService = new OrderServiceImpl(
            new JdbcOrderRepositoryImpl(),
              new JdbcShippingAddressRepositoryImpl(),
                new JdbcCartRepositoryImpl()
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addCartUi(req);
        req.setAttribute("countries", getCountries());

        req.getRequestDispatcher("/WEB-INF/order.jsp")
                .forward(req, resp);
    }

    private List<String> getCountries() {
        return Arrays.asList("Bangladesh", "Switzerland", "Japan", "USA", "Canada");
    }

    private void addCartUi(HttpServletRequest req) {
        if(SecurityContext.isAuthenticated(req)) {
            UserDto currentUser = SecurityContext.getCurrentUser(req);
            Cart cart = cartService.getCartByUser(currentUser);

            req.setAttribute("cart", cart);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Handle order request form");

        ShippingAddressDto shippingAddress = copyParametersTo(req);

        LOGGER.info("Shipping address information: {}", shippingAddress);

        Map<String, String> errors = ValidationUtil.getInstance().validate(shippingAddress);

        if(!errors.isEmpty()) {
            req.setAttribute("countries", getCountries());
            req.setAttribute("errors", errors);
            req.setAttribute("shippingAddress", shippingAddress);
            addCartUi(req);

            req.getRequestDispatcher("/WEB-INF/order.jsp")
                    .forward(req, resp);
        } else {
            orderService.processOrder(shippingAddress, SecurityContext.getCurrentUser(req));

            resp.sendRedirect("/home?orderSuccess=true");
        }
    }

    private ShippingAddressDto copyParametersTo(HttpServletRequest req) {
        ShippingAddressDto shippingAddress = new ShippingAddressDto();

        shippingAddress.setAddress(req.getParameter("address"));
        shippingAddress.setAddress2(req.getParameter("address2"));
        shippingAddress.setCountry(req.getParameter("country"));
        shippingAddress.setState(req.getParameter("state"));
        shippingAddress.setZip(req.getParameter("zip"));
        shippingAddress.setMobileNumber(req.getParameter("mobileNumber"));

        return shippingAddress;
    }
}
