package com.example.sportbet.service;

import com.example.sportbet.model.Transaction;
import com.example.sportbet.model.Wallet;
import com.example.sportbet.model.enums.TransactionType;
import com.example.sportbet.repository.UserRepository;
import com.example.sportbet.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Page<Transaction> getTransactionsForUser(Long userId, Pageable pageable) {
        logger.info("Fetching paginated transactions for userId: {}, page: {}, size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        if (!userRepository.existsById(userId)) {
            logger.error("User with ID {} not found", userId);
            throw new IllegalArgumentException("User with ID " + userId + " not found.");
        }

        Page<Transaction> transactions = transactionRepository.findByUserId(userId, pageable);
        logger.info("Found {} transactions for userId: {} on page: {}",
                transactions.getTotalElements(), userId, pageable.getPageNumber());
        return transactions;
    }

    public Transaction createBetTransaction(Wallet wallet, BigDecimal betAmount) {
        logger.info("Creating bet transaction for walletId: {}, amount: {}", wallet.getId(), betAmount);
        Transaction betTransaction = Transaction.builder()
                .type(TransactionType.BET)
                .amount(betAmount.negate())
                .wallet(wallet)
                .build();
        transactionRepository.save(betTransaction);
        logger.info("Bet transaction created with amount: {} for walletId: {}", betAmount.negate(), wallet.getId());
        return betTransaction;
    }

    public void updateWalletAndTransactions(Wallet wallet, BigDecimal winnings) {
        logger.info("Updating walletId: {} with winnings: {}", wallet.getId(), winnings);

        if (winnings.compareTo(BigDecimal.ZERO) > 0) {
            Transaction winTransaction = Transaction.builder()
                    .type(TransactionType.WIN)
                    .amount(winnings)
                    .wallet(wallet)
                    .build();
            transactionRepository.save(winTransaction);
            logger.info("Win transaction created with amount: {} for walletId: {}", winnings, wallet.getId());
        } else {
            logger.warn("No winnings to update for walletId: {}", wallet.getId());
        }
    }
}
