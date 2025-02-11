package com.example.sportbet.controller;

import com.example.sportbet.dto.ApiResponse;
import com.example.sportbet.dto.response.BetHistoryResponseDto;
import com.example.sportbet.dto.request.BetRequestDto;
import com.example.sportbet.dto.response.GameResultResponseDto;
import com.example.sportbet.service.BetService;
import com.example.sportbet.service.GameService;
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
    public ResponseEntity<ApiResponse<GameResultResponseDto>> placeBet(@RequestBody BetRequestDto betRequestDto) {
        logger.info("Placing a bet: userId={}, betAmount={}, betNumber={}, gameType={}",
                betRequestDto.getUserId(), betRequestDto.getBetAmount(), betRequestDto.getBetNumber(), betRequestDto.getGameType());

        GameResultResponseDto result = gameService.playGame(
                betRequestDto.getUserId(),
                betRequestDto.getBetAmount(),
                betRequestDto.getBetNumber(),
                betRequestDto.getGameType()
        );

        logger.info("Bet placed successfully: {}", result);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bet placed successfully", result));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<Page<BetHistoryResponseDto>>>getBetHistory(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Requesting bet history: userId={}, page={}, size={}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<BetHistoryResponseDto> betHistory = betService.getBetHistoryForUser(userId, pageable);

        logger.info("Bet history retrieved successfully for userId: {}", userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bet history retrieved successfully", betHistory));
    }
}