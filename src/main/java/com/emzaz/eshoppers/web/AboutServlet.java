package com.emzaz.eshoppers.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AboutServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(AboutServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Serving About Page");

        req.getRequestDispatcher("/WEB-INF/about.jsp")
                .forward(req, resp);
    }
}
