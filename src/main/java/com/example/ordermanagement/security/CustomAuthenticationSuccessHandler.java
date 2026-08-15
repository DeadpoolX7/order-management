package com.example.ordermanagement.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );

        if (!isAdmin) {
            response.sendRedirect("/products");
            return;
        }

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        if (!principal.isTotpEnabled()) {
            response.sendRedirect("/admin/mfa/setup");
            return;
        }

        response.sendRedirect("/admin/totp");
    }
}