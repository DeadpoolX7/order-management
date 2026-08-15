package com.example.ordermanagement.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Service
public class TotpAuthenticationService {

    public void markTotpVerified(
            Authentication authentication,
            HttpServletRequest request
    ) {

        List<GrantedAuthority> authorities =
                new ArrayList<>(
                        authentication.getAuthorities()
                );

        authorities.add(
                new TotpGrantedAuthority()
        );

        UsernamePasswordAuthenticationToken
                authenticatedToken =
                new UsernamePasswordAuthenticationToken(
                        authentication.getPrincipal(),
                        authentication.getCredentials(),
                        authorities
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(
                authenticatedToken
        );

        SecurityContextHolder.setContext(context);

        new HttpSessionSecurityContextRepository()
                .saveContext(
                        context,
                        request,
                        null
                );
    }
}