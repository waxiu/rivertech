package com.example.rivertech.controller;

import com.example.rivertech.dto.PlayerRegistrationDto;
import com.example.rivertech.model.Bet;
import com.example.rivertech.model.Player;
import com.example.rivertech.model.Transaction;
import com.example.rivertech.service.BetService;
import com.example.rivertech.service.PlayerService;
import com.example.rivertech.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;
    private final TransactionService transactionService;
    private final BetService betService;

    public PlayerController(PlayerService playerService, TransactionService transactionService, BetService betService) {
        this.playerService = playerService;
        this.transactionService = transactionService;
        this.betService = betService;
    }

    @PostMapping("/register")
    public ResponseEntity<Player> registerPlayer(@RequestBody PlayerRegistrationDto dto) {
        Player registeredPlayer = playerService.registerPlayer(dto);
        return ResponseEntity.ok(registeredPlayer);
    }
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Transaction>> getTransactionsForPlayer(@PathVariable Long playerId) {
        List<Transaction> transactions = transactionService.getTransactionsForPlayer(playerId);
        return ResponseEntity.ok(transactions);
    }
    @GetMapping("/{playerId}/bets")
    public List<Bet> getBets(@PathVariable long playerId) {
        return betService.getBetsForPlayer(playerId);
    }
}
