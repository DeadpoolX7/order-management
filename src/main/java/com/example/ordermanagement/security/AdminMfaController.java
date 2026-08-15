package com.example.ordermanagement.security;

import com.example.ordermanagement.user.User;
import com.example.ordermanagement.user.UserService;

import dev.samstevens.totp.exceptions.QrGenerationException;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/mfa")
public class AdminMfaController {

    private final UserService userService;
    private final TotpService totpService;

    public AdminMfaController(
            UserService userService,
            TotpService totpService
    ) {
        this.userService = userService;
        this.totpService = totpService;
    }

    @GetMapping("/setup")
    public String setup(
            @AuthenticationPrincipal UserPrincipal principal,
            Model model
    ) {

        User user = principal.getUser();

        if (user.isTotpEnabled()) {
            return "redirect:/admin/mfa";
        }

        String secret = user.getTotpSecret();

        if (secret == null || secret.isBlank()) {
            secret = totpService.generateSecret();

            userService.saveTotpSecret(
                    user.getId(),
                    secret
            );
            user.setTotpSecret(secret);
        }

        String qrCode = null;
        try {
            qrCode = totpService.generateQrCode(
                    user.getEmail(),
                    secret
            );
        } catch (QrGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        model.addAttribute(
                "qrCode",
                qrCode
        );

        return "admin/mfa-setup";
    }

    @GetMapping
    public String status(
            @AuthenticationPrincipal UserPrincipal principal,
            Model model
    ) {

        model.addAttribute(
                "enabled",
                principal.getUser().isTotpEnabled()
        );

        return "admin/mfa-status";
    }

    @PostMapping("/verify-setup")
    public String verifySetup(
            @AuthenticationPrincipal UserPrincipal principal,

            @RequestParam
            @Pattern(
                    regexp = "\\d{6}",
                    message = "Enter a valid 6-digit code"
            )
            String code
    ) {

        //User user = principal.getUser();
        User user = userService.findByEmail(principal.getEmail());

        String secret = user.getTotpSecret();

        if (secret == null ||
                !totpService.verifyCode(secret, code)) {

            return "redirect:/admin/mfa/setup?error";
        }

        userService.enableTotp(
                user.getId()
        );
        principal.getUser().setTotpEnabled(true); 

        return "redirect:/admin/mfa?enabled";
    }
}