package com.example.sportbet.service;

import com.example.sportbet.model.User;
import com.example.sportbet.model.Wallet;
import com.example.sportbet.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final WalletService walletService;

    public UserService(UserRepository userRepository, WalletService walletService) {
        this.userRepository = userRepository;
        this.walletService = walletService;
    }

    public void depositToUserWallet(Long userId, BigDecimal amount) {
        logger.info("Starting deposit for userId: {} with amount: {}", userId, amount);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new IllegalArgumentException("User not found with ID: " + userId);
                });
        Wallet wallet = user.getWallet();
        walletService.depositFunds(wallet, amount);
        logger.info("Deposit to userId: {} completed successfully", user);
    }
}
