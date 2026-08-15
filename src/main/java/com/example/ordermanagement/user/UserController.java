package com.example.ordermanagement.user;

import com.example.ordermanagement.user.dto.RegistrationForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute(
                "registrationForm",
                new RegistrationForm()
        );

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registrationForm")
            RegistrationForm registrationForm,

            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {

            userService.register(registrationForm);

        } catch (IllegalArgumentException exception) {

            bindingResult.reject(
                    "registration.failed",
                    exception.getMessage()
            );

            return "auth/register";
        }

        return "redirect:/login?registered";
    }
}