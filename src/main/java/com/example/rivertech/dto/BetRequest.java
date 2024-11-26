package com.example.rivertech.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BetRequest {
    private Long playerId;
    private BigDecimal betAmount;
    private int betNumber;
    private String gameType;
}
