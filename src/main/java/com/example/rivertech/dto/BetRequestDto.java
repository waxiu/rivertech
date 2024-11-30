package com.example.rivertech.dto;

import com.example.rivertech.game.enums.GameType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BetRequestDto {
    private Long playerId;
    private BigDecimal betAmount;
    private int betNumber;
    private GameType gameType;
}
