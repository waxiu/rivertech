package com.example.sportbet.dto;

import com.example.sportbet.game.enums.GameType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BetRequestDto {
    private Long userId;
    private BigDecimal betAmount;
    private int betNumber;
    private GameType gameType;
}
