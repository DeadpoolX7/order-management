package com.example.ordermanagement.security;

import org.springframework.security.core.GrantedAuthority;

public final class TotpGrantedAuthority
        implements GrantedAuthority {

    public static final String AUTHORITY = "FACTOR_TOTP";

    @Override
    public String getAuthority() {
        return AUTHORITY;
    }
}