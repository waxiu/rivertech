package com.example.rivertech.service;

import com.example.rivertech.model.Transaction;
import com.example.rivertech.model.Wallet;
import com.example.rivertech.model.enums.TransactionType;
import com.example.rivertech.repository.PlayerRepository;
import com.example.rivertech.repository.TransactionRepository;
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
    private final PlayerRepository playerRepository;

    public TransactionService(TransactionRepository transactionRepository, PlayerRepository playerRepository) {
        this.transactionRepository = transactionRepository;
        this.playerRepository = playerRepository;
    }

    public Page<Transaction> getTransactionsForPlayer(Long playerId, Pageable pageable) {
        logger.info("Fetching paginated transactions for playerId: {}, page: {}, size: {}",
                playerId, pageable.getPageNumber(), pageable.getPageSize());

        if (!playerRepository.existsById(playerId)) {
            logger.error("Player with ID {} not found", playerId);
            throw new IllegalArgumentException("Player with ID " + playerId + " not found.");
        }

        Page<Transaction> transactions = transactionRepository.findByPlayerId(playerId, pageable);
        logger.info("Found {} transactions for playerId: {} on page: {}",
                transactions.getTotalElements(), playerId, pageable.getPageNumber());
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
