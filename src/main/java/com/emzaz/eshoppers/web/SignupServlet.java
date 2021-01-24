package com.emzaz.eshoppers.web;

import com.emzaz.eshoppers.dtos.UserDto;
import com.emzaz.eshoppers.repository.JdbcUserRepositoryImpl;
import com.emzaz.eshoppers.repository.UserRepositoryImpl;
import com.emzaz.eshoppers.service.UserService;
import com.emzaz.eshoppers.service.UserServiceImpl;
import com.emzaz.eshoppers.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class SignupServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignupServlet.class);

    private UserService userService = new UserServiceImpl(new JdbcUserRepositoryImpl());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Serving Sign up Servlet");

        req.getRequestDispatcher("/WEB-INF/signup.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDto userDto = copyParametersTo(req);

        Map<String, String> errors = ValidationUtil.getInstance().validate(userDto);

        if(!errors.isEmpty()) {
            req.setAttribute("userDto", userDto);
            req.setAttribute("errors", errors);
            LOGGER.info("User sent invalid data: {}", userDto);

            req.getRequestDispatcher("/WEB-INF/signup.jsp")
                    .forward(req, resp);
        } else if(userService.isNotUniqueUsername(userDto)) {
            LOGGER.info("Username : {} is already exist.", userDto.getUsername());

            errors.put("username", "The username is already exists. Please enter a different username.");
            req.setAttribute("userDto", userDto);
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/WEB-INF/signup.jsp")
                    .forward(req, resp);
        } else if(userService.isNotUniqueEmail(userDto)) {
            LOGGER.info("Email : {} is already exist.", userDto.getEmail());

            errors.put("email", "The email is already exist. Please enter a different email.");
            req.setAttribute("userDto", userDto);
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/WEB-INF/signup.jsp")
                    .forward(req, resp);
        } else if(userService.isNotUniqueFirstName(userDto)) {
            LOGGER.info("FirstName : {} is already exist.", userDto.getFirstName());

            errors.put("firstName", "The firstName is already exists. Please enter a different firstName.");
            req.setAttribute("userDto", userDto);
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/WEB-INF/signup.jsp")
                    .forward(req, resp);
        } else if(userService.isNotUniqueLastName(userDto)) {
            LOGGER.info("LastName : {} is already exist.", userDto.getLastName());

            errors.put("lastName", "The lastName already exists. Please enter a different lastName.");
            req.setAttribute("userDto", userDto);
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/WEB-INF/signup.jsp")
                    .forward(req, resp);
        } else {
            LOGGER.info("User is valid, creating a new user with: {}", userDto);

            userService.saveUser(userDto);
            resp.sendRedirect("/login");
        }
    }

    private UserDto copyParametersTo(HttpServletRequest req) {
        UserDto userDto = new UserDto();

        userDto.setUsername(req.getParameter("username"));
        userDto.setEmail(req.getParameter("email"));
        userDto.setPassword(req.getParameter("password"));
        userDto.setFirstName(req.getParameter("firstName"));
        userDto.setLastName(req.getParameter("lastName"));

        return userDto;
    }
}
