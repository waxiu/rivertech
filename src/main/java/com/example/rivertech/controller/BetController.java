package com.example.rivertech.controller;

import com.example.rivertech.dto.BetHistory;
import com.example.rivertech.dto.BetRequest;
import com.example.rivertech.dto.GameResult;
import com.example.rivertech.service.BetService;
import com.example.rivertech.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bet")
public class BetController {
    private static final Logger logger = LoggerFactory.getLogger(BetController.class);
    private final GameService gameService;
    private final BetService betService;

    public BetController(GameService gameService, BetService betService) {
        this.gameService = gameService;
        this.betService = betService;
    }

    @PostMapping
    public ResponseEntity<GameResult> placeBet(@RequestBody BetRequest betRequest) {
        GameResult result = gameService.playGame(
                betRequest.getPlayerId(),
                betRequest.getBetAmount(),
                betRequest.getBetNumber(),
                betRequest.getGameType()
        );
        return ResponseEntity.ok(result);
    }
    @GetMapping("/history/{playerId}")
    public ResponseEntity<Page<BetHistory>> getBetHistory(
            @PathVariable long playerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Request for paginated bet history for playerId: {}, page: {}, size: {}", playerId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<BetHistory> betHistory = betService.getBetHistoryForPlayer(playerId, pageable);

        return ResponseEntity.ok(betHistory);
    }
}

