package com.example.sportbet.service;

import com.example.sportbet.configuration.security.JwtService;
import com.example.sportbet.dto.response.AuthResponseDto;
import com.example.sportbet.dto.request.LoginRequestDto;
import com.example.sportbet.dto.response.UserRegistrationRequestDto;
import com.example.sportbet.model.User;
import com.example.sportbet.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final WalletService walletService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, WalletService walletService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.walletService = walletService;
    }

    public User registerUser(UserRegistrationRequestDto dto) {
        logger.info("Registering new user with username: {}", dto.getUsername());

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            logger.warn("Username '{}' already exists", dto.getUsername());
            throw new IllegalArgumentException("Username '" + dto.getUsername() + "' is already taken.");
        }

        User user = User.builder()
                .name(dto.getName())
                .surname(dto.getSurname())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .build();

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with userId: {}", savedUser.getId());

        walletService.createWalletForUser(user);

        return savedUser;
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()))
        );

        String token = jwtService.generateToken(userDetails);
        return new AuthResponseDto(token);
    }
}

