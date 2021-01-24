package com.emzaz.eshoppers.web;

import com.emzaz.eshoppers.dtos.LoginDto;
import com.emzaz.eshoppers.dtos.UserDto;
import com.emzaz.eshoppers.repository.JdbcUserRepositoryImpl;
import com.emzaz.eshoppers.repository.UserRepositoryImpl;
import com.emzaz.eshoppers.service.UserService;
import com.emzaz.eshoppers.service.UserServiceImpl;
import com.emzaz.eshoppers.util.SecurityContext;
import com.emzaz.eshoppers.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Map;

public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class);

    private UserService userService = new UserServiceImpl(new JdbcUserRepositoryImpl());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Serving Login Page");
        String logout = req.getParameter("logout");

        if (logout != null && Boolean.parseBoolean(logout)) {
            req.setAttribute("message", "You have been successfully logged out.");
        }

        req.getRequestDispatcher("/WEB-INF/login.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoginDto loginDto = new LoginDto(req.getParameter("username"), req.getParameter("password"));

        LOGGER.info("Received login data: {}", loginDto);

        Map<String, String> errors = ValidationUtil.getInstance().validate(loginDto);

        if(!errors.isEmpty()) {
            LOGGER.info("Failed to login, sending login page again.");

            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/WEB-INF/login.jsp")
                    .forward(req, resp);
        }

        try {
            login(loginDto, req);

            LOGGER.info("Successfully Logged in, redirecting to home page.");
            resp.sendRedirect("/home");
        } catch (UserPrincipalNotFoundException e) {
            LOGGER.info("Incorrect username/ password.");

            errors.put("username", "Incorrect username/ password");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/WEB-INF/login.jsp")
                    .forward(req, resp);
        }
    }

    private void login(LoginDto loginDto, HttpServletRequest req) throws UserPrincipalNotFoundException {
        UserDto userDto = userService.verifyUser(loginDto);

        SecurityContext.login(req, userDto);
    }
}
