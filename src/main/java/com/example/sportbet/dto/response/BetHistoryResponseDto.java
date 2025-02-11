package com.example.sportbet.dto.response;

import com.example.sportbet.model.enums.BetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BetHistoryResponseDto {
    private BigDecimal betAmount;
    private int betNumber;
    private int generatedNumber;
    private BigDecimal winnings;
    private BetStatus status;
}


