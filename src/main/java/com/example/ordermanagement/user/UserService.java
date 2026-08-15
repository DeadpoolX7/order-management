package com.example.ordermanagement.user;

import com.example.ordermanagement.user.dto.RegistrationForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegistrationForm form) {

        String email = form.getEmail()
                .trim()
                .toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists"
            );
        }

        User user = new User();

        user.setEmail(email);

        user.setPassword(
                passwordEncoder.encode(form.getPassword())
        );

        user.setFirstName(
                form.getFirstName().trim()
        );

        user.setLastName(
                form.getLastName().trim()
        );

        user.setRole(Role.USER);

        user.setTotpEnabled(false);
        user.setTotpSecret(null);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {

        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    public void saveTotpSecret(
        Long userId,
        String secret
) {

    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "User not found"
                    )
            );

    user.setTotpSecret(secret);

    userRepository.save(user);
    }

    public void enableTotp(Long userId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "User not found"
                    )
            );

    user.setTotpEnabled(true);

    userRepository.save(user);
    }

}