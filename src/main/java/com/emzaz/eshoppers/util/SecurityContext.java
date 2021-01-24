package com.emzaz.eshoppers.util;

import com.emzaz.eshoppers.dtos.UserDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SecurityContext {
    public static final String AUTHENTICATION_KEY = "auth.key";

    public static void login(HttpServletRequest req, UserDto userDto) {
        HttpSession oldSession = req.getSession(false);
        if(oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session = req.getSession(true);
        session.setAttribute(AUTHENTICATION_KEY, userDto);
    }

    public static void logout(HttpServletRequest req) {
        HttpSession session = req.getSession(true);

        session.removeAttribute(AUTHENTICATION_KEY);
    }

    public static UserDto getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(true);

        return (UserDto) session.getAttribute(AUTHENTICATION_KEY);
    }

    public static boolean isAuthenticated(HttpServletRequest req) {
        HttpSession session = req.getSession(true);

        return session.getAttribute(AUTHENTICATION_KEY) != null;
    }
}
