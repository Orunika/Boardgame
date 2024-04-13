package com.javaproject.security;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class LoggingAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger = Logger.getLogger(LoggingAccessDeniedHandler.class.getName());

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // get the user from the security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Log the access denied attempt
        if (auth != null) {
            String format = "%s was denied access to %s\n";
            logger.log(Level.INFO, String.format(format, auth.getName(), request.getRequestURI()));
        }

        // redirect to the permission-denied page
        response.sendRedirect("/permission-denied");
    }
}
