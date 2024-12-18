package com.example.rivertech.controller;

import com.example.rivertech.dto.BetHistoryDto;
import com.example.rivertech.dto.BetRequestDto;
import com.example.rivertech.dto.GameResultDto;
import com.example.rivertech.service.BetService;
import com.example.rivertech.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



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


    @PostMapping("/place")
    public ResponseEntity<GameResultDto> placeBet(@RequestBody BetRequestDto betRequestDto) {
        logger.info("Placing a bet: playerId={}, betAmount={}, betNumber={}, gameType={}",
                betRequestDto.getPlayerId(), betRequestDto.getBetAmount(), betRequestDto.getBetNumber(), betRequestDto.getGameType());

        GameResultDto result = gameService.playGame(
                betRequestDto.getPlayerId(),
                betRequestDto.getBetAmount(),
                betRequestDto.getBetNumber(),
                betRequestDto.getGameType()
        );

        logger.info("Bet placed successfully: {}", result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/{playerId}")
    public ResponseEntity<Page<BetHistoryDto>> getBetHistory(
            @PathVariable long playerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Requesting bet history: playerId={}, page={}, size={}", playerId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<BetHistoryDto> betHistory = betService.getBetHistoryForPlayer(playerId, pageable);

        logger.info("Bet history retrieved successfully for playerId: {}", playerId);
        return ResponseEntity.ok(betHistory);
    }
}