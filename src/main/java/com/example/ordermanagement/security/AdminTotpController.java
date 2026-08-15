package com.example.ordermanagement.security;

import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.ordermanagement.user.User;
import com.example.ordermanagement.user.UserService;

@Controller
@RequestMapping("/admin/totp")
public class AdminTotpController {

        private final TotpService totpService;
        private final TotpAuthenticationService authenticationService;

        private final UserService userService;

        public AdminTotpController(
                        TotpService totpService,
                        TotpAuthenticationService authenticationService,
                        UserService userService) {
                this.totpService = totpService;
                this.authenticationService = authenticationService;
                this.userService = userService;
        }

        @GetMapping
        public String challenge() {
                return "admin/totp";
        }

        @PostMapping("/verify")
        public String verify(
                        @AuthenticationPrincipal UserPrincipal principal,
                        @RequestParam @Pattern(regexp = "\\d{6}", message = "Code must contain 6 digits") String code,

                        org.springframework.security.core.Authentication authentication,

                        jakarta.servlet.http.HttpServletRequest request) {

                // if (!totpService.verifyCode(
                // principal.getUser().getTotpSecret(),
                // code
                // )) {

                // return "redirect:/admin/totp?error";
                // }

                User user = userService.findByEmail(principal.getUsername());
                if (user.getTotpSecret() == null || !totpService.verifyCode(user.getTotpSecret(), code)) {
                        return "redirect:/admin/totp?error";
                }

                authenticationService.markTotpVerified(
                                authentication,
                                request);

                return "redirect:/products";
        }
}