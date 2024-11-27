package com.example.rivertech.service;

import com.example.rivertech.model.Transaction;
import com.example.rivertech.model.Wallet;
import com.example.rivertech.model.enums.TransactionType;
import com.example.rivertech.repository.PlayerRepository;
import com.example.rivertech.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PlayerRepository playerRepository;

    public TransactionService(TransactionRepository transactionRepository, PlayerRepository playerRepository) {
        this.transactionRepository = transactionRepository;
        this.playerRepository = playerRepository;
    }

    public List<Transaction> getTransactionsForPlayer(Long playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new IllegalArgumentException("Player with ID " + playerId + " not found.");
        }

        return transactionRepository.findByPlayerId(playerId);
    }

    public void createBetTransaction(Wallet wallet, BigDecimal betAmount) {
        Transaction betTransaction = Transaction.builder()
                .type(TransactionType.BET)
                .amount(betAmount.negate())
                .wallet(wallet)
                .build();
        transactionRepository.save(betTransaction);
    }

    public void updateWalletAndTransactions(Wallet wallet, BigDecimal winnings) {
        if (winnings.compareTo(BigDecimal.ZERO) > 0) {
            Transaction winTransaction = Transaction.builder()
                    .type(TransactionType.WIN)
                    .amount(winnings)
                    .wallet(wallet)
                    .build();
            transactionRepository.save(winTransaction);
        }
    }
}
