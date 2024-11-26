package com.example.rivertech.controller;

import com.example.rivertech.dto.BetRequest;
import com.example.rivertech.dto.GameResult;
import com.example.rivertech.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bet")
public class BetController {

    private final GameService gameService;

    public BetController(GameService gameService) {
        this.gameService = gameService;
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
}

