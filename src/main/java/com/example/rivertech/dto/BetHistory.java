package com.example.rivertech.dto;

import com.example.rivertech.model.enums.BetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BetHistory {
    private BigDecimal betAmount;
    private int betNumber;
    private int generatedNumber;
    private BigDecimal winnings;
    private BetStatus status;
}


