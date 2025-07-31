package com.elbit.core.services.service;

import com.elbit.core.services.data.User;
import com.elbit.core.services.dto.UserRequest;
import com.elbit.core.services.exceptions.InvalidCredentialsException;
import com.elbit.core.services.exceptions.UserNotFoundException;
import com.elbit.core.services.exceptions.UsernameAlreadyExistsException;
import com.elbit.core.services.jwt.JwtUtil;
import com.elbit.core.services.mapper.UserMapper;
import com.elbit.core.services.repository.UserRepository;
import com.elbit.core.services.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<String> register(UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists: " + user.getUsername());
        }
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Registered: " + user.getUsername());
    }

    public ResponseEntity<String> login(UserRequest userRequest) {
        User userLogin = userMapper.toEntity(userRequest);
        User user = userRepository.findByUsername(userLogin.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!checkPassword(userLogin.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials for user: " + userLogin.getUsername());
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(token);
    }

    private boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
