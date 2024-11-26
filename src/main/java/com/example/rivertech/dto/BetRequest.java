package com.example.rivertech.dto;

import com.example.rivertech.game.enums.GameType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BetRequest {
    private Long playerId;
    private BigDecimal betAmount;
    private int betNumber;
    private GameType gameType;
}
