package com.example.sportbet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class GameResultResponseDto {
    private final int generatedNumber;
    private final BigDecimal winnings;
}

