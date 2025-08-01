package com.elbit.core.services.service;

import com.elbit.core.services.data.Audit;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducer kafkaProducer;

    public ResponseEntity<String> register(UserRequest userRequest) {
        log.info("Registering user: {}", userRequest.getUsername());
        User user = userMapper.toEntity(userRequest);
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists: " + user.getUsername());
        }
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRepository.save(user);
        sendKafkaMessage("USER_REGISTERED", "User registered: " + user.getUsername());
        return ResponseEntity.ok("Registered: " + user.getUsername());
    }

    public ResponseEntity<String> login(UserRequest userRequest) {
        log.info("Logging in user: {}", userRequest.getUsername());
        User userLogin = userMapper.toEntity(userRequest);
        User user = userRepository.findByUsername(userLogin.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!checkPassword(userLogin.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials for user: " + userLogin.getUsername());
        }

        String token = jwtUtil.generateToken(user.getUsername());
        sendKafkaMessage("USER_LOGIN", "User logged in: " + user.getUsername());
        return ResponseEntity.ok(token);
    }

    private void sendKafkaMessage(String event, String info) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        kafkaProducer.sendMessage("AUDIT-IN", Audit.builder()
                .event(event)
                .date(now.format(formatter))
                .info(info)
                .build());
    }

    private boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
