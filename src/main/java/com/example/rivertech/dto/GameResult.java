package com.example.rivertech.dto;

import java.math.BigDecimal;

public class GameResult {

    private final int generatedNumber;
    private final BigDecimal winnings;

    public GameResult(int generatedNumber, BigDecimal winnings) {
        this.generatedNumber = generatedNumber;
        this.winnings = winnings;
    }

    public int getGeneratedNumber() {
        return generatedNumber;
    }

    public BigDecimal getWinnings() {
        return winnings;
    }

}

