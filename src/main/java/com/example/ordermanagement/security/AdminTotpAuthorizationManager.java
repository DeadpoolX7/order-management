package com.example.ordermanagement.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

@Component
public class AdminTotpAuthorizationManager
        implements AuthorizationManager<RequestAuthorizationContext> {

    @Override    
    public AuthorizationResult authorize(
            Supplier<? extends Authentication> authentication,
            RequestAuthorizationContext context
    ) {

            
            Authentication auth =
            authentication.get();
            
            if (auth == null || !auth.isAuthenticated()) {
                return new AuthorizationDecision(false);
            }
        boolean isAdmin =
                auth.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );

        boolean totpVerified =
                auth.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("FACTOR_TOTP")
                        );

        return new AuthorizationDecision(
                auth.isAuthenticated()
                        && isAdmin
                        && totpVerified
        );
    }


}