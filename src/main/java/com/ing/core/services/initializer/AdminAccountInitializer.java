package com.ing.core.services.initializer;

import com.ing.core.services.data.User;
import com.ing.core.services.repository.UserRepository;
import com.ing.core.services.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String defaultUsername = Optional.ofNullable(System.getenv("DEFAULT_ADMIN_USER"))
                .orElseThrow(() -> new IllegalStateException("DEFAULT_ADMIN_USER environment variable not set"));

        String defaultPassword = Optional.ofNullable(System.getenv("DEFAULT_ADMIN_PASS"))
                .orElseThrow(() -> new IllegalStateException("DEFAULT_ADMIN_PASS environment variable not set"));

        if (userRepository.findByUsername(defaultUsername).isEmpty()) {
            User admin = new User();
            admin.setUsername(defaultUsername);
            admin.setPassword(passwordEncoder.encode(defaultPassword));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }
}