package com.emzaz.eshoppers.filter;


import com.emzaz.eshoppers.util.SecurityContext;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;

@WebFilter(urlPatterns = "/*")
public class AuthFilter implements Filter {
    private static final String[] ALLOWED_CONTENTS = {".css", ".js", ".jpg", "home", "login", "signup", ".png"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestedUri = httpServletRequest.getRequestURI();

        boolean allowed = Stream.of(ALLOWED_CONTENTS).anyMatch(requestedUri::contains);

        if(requestedUri.equals("/") || allowed || SecurityContext.isAuthenticated(httpServletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            ((HttpServletResponse) servletResponse).sendRedirect("/login");
        }
    }
}
