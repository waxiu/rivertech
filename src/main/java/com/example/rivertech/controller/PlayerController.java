package com.example.rivertech.controller;

import com.example.rivertech.dto.DepositRequestDto;
import com.example.rivertech.dto.PlayerRegistrationDto;
import com.example.rivertech.model.Bet;
import com.example.rivertech.model.Player;
import com.example.rivertech.model.Transaction;
import com.example.rivertech.service.BetService;
import com.example.rivertech.service.PlayerService;
import com.example.rivertech.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/player")
public class PlayerController {

    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);
    private final PlayerService playerService;
    private final TransactionService transactionService;
    private final BetService betService;

    public PlayerController(PlayerService playerService, TransactionService transactionService, BetService betService) {
        this.playerService = playerService;
        this.transactionService = transactionService;
        this.betService = betService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerPlayer(@RequestBody PlayerRegistrationDto dto) {
        Player registeredPlayer = playerService.registerPlayer(dto);
        return ResponseEntity.ok("Player registered successfully");
    }
    @GetMapping("/transactions/{playerId}")
    public ResponseEntity<Page<Transaction>> getTransactionsForPlayer(
            @PathVariable Long playerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Request for paginated transactions for playerId: {}, page: {}, size: {}", playerId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getTransactionsForPlayer(playerId, pageable);

        return ResponseEntity.ok(transactions);
    }
    @GetMapping("/bets/{playerId}")
    public ResponseEntity<Page<Bet>> getBetsForPlayer(
            @PathVariable Long playerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Bet> bets = betService.getBetsForPlayer(playerId, pageable);
        return ResponseEntity.ok(bets);
    }

    @PostMapping("/deposit/{playerId}")
    public ResponseEntity<String> depositToWallet(
            @PathVariable Long playerId,
            @RequestBody DepositRequestDto depositRequestDto) {
        logger.info("Deposit request received for playerId: {} with amount: {}", playerId, depositRequestDto.getAmount());
        try {
            playerService.depositToPlayerWallet(playerId, depositRequestDto.getAmount());
            logger.info("Deposit successful for playerId: {}", playerId);
            return ResponseEntity.ok("Deposit successful");
        } catch (IllegalArgumentException e) {
            logger.error("Error during deposit: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
