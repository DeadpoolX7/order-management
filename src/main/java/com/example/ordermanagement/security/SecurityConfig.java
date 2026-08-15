package com.example.ordermanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler = new CustomAuthenticationSuccessHandler();

        

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .authorizeHttpRequests(authorize -> authorize

    .requestMatchers(
            "/login",
            "/register",
            "/access-denied"
    ).permitAll()

    // TOTP setup/challenge pages require admin
    // password authentication, but not TOTP yet.
    .requestMatchers(
            "/admin/mfa/**",
            "/admin/totp/**"
    ).hasRole("ADMIN")

    // Normal product browsing
    .requestMatchers(
            "/products"
    ).hasAnyRole("USER", "ADMIN")

    // Administrative product operations
    .requestMatchers(
            "/products/new",
            "/products/save",
            "/products/{id}/edit",
            "/products/{id}/delete",
            "/inventory/**"
    ).access(
            new AdminTotpAuthorizationManager()
    )

    .anyRequest().authenticated()
)

            .formLogin(form -> form
                .loginPage("/login")
                //.defaultSuccessUrl("/products", false)
                .successHandler(authenticationSuccessHandler)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )

            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}