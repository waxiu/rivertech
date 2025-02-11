package com.example.sportbet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class GameResultDto {
    private final int generatedNumber;
    private final BigDecimal winnings;
}

