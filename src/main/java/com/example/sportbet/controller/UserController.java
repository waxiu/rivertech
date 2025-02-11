package com.example.sportbet.controller;

import com.example.sportbet.dto.*;
import com.example.sportbet.model.Transaction;
import com.example.sportbet.model.User;
import com.example.sportbet.service.BetService;
import com.example.sportbet.service.UserService;
import com.example.sportbet.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final TransactionService transactionService;
    private final BetService betService;

    public UserController(UserService userService, TransactionService transactionService, BetService betService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.betService = betService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@RequestBody UserRegistrationDto dto) {
        User registeredUser = userService.registerUser(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody @Valid LoginRequestDto request) {
        AuthResponseDto authResponseDto = userService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User logged successfully", authResponseDto));
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<ApiResponse<Page<Transaction>>> getTransactionsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Request for paginated transactions for userId: {}, page: {}, size: {}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getTransactionsForUser(userId, pageable);

        return ResponseEntity.ok(new ApiResponse<>(true, "Transactions fetched successfully", transactions));
    }

    @PostMapping("/deposit/{userId}")
    public ResponseEntity<ApiResponse<String>> depositToWallet(
            @PathVariable Long userId,
            @RequestBody DepositRequestDto depositRequestDto) {
        logger.info("Deposit request received for userId: {} with amount: {}", userId, depositRequestDto.getAmount());
        userService.depositToUserWallet(userId, depositRequestDto.getAmount());
            logger.info("Deposit successful for userId: {}", userId);
            return ResponseEntity.ok(new ApiResponse<>(true,"Deposit successful","success"));
    }
}
