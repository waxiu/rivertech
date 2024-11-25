package com.example.rivertech.service;

import com.example.rivertech.model.Transaction;
import com.example.rivertech.repository.PlayerRepository;
import com.example.rivertech.repository.TransactionRepository;
import org.springframework.stereotype.Service;

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
        // Sprawdź, czy gracz istnieje
        if (!playerRepository.existsById(playerId)) {
            throw new IllegalArgumentException("Player with ID " + playerId + " not found.");
        }

        // Pobierz transakcje gracza
        return transactionRepository.findByPlayerId(playerId);
    }
}
