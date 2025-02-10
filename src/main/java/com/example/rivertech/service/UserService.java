package com.example.rivertech.service;

import com.example.rivertech.configuration.security.JwtService;
import com.example.rivertech.dto.AuthResponseDto;
import com.example.rivertech.dto.LoginRequestDto;
import com.example.rivertech.dto.UserRegistrationDto;
import com.example.rivertech.model.User;
import com.example.rivertech.model.Wallet;
import com.example.rivertech.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final WalletService walletService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, WalletService walletService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationDto dto) {
        logger.info("Registering new user with username: {}", dto.getUsername());

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            logger.warn("Username '{}' already exists", dto.getUsername());
            throw new IllegalArgumentException("Username '" + dto.getUsername() + "' is already taken.");
        }

        User user = User.builder()
                .name(dto.getName())
                .surname(dto.getSurname())
                .username(dto.getUsername())
                .build();

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with userId: {}", savedUser.getId());

        walletService.createWalletForUser(user);

        return savedUser;
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()))
        );

        String token = jwtService.generateToken(userDetails);
        return new AuthResponseDto(token);
    }

    public void depositToUserWallet(Long userId, BigDecimal amount) {
        logger.info("Starting deposit for userId: {} with amount: {}", userId, amount);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", user);
                    return new IllegalArgumentException("User not found with ID: " + user);
                });
        Wallet wallet = user.getWallet();
        walletService.depositFunds(wallet, amount);
        logger.info("Deposit to userId: {} completed successfully", user);
    }
}
