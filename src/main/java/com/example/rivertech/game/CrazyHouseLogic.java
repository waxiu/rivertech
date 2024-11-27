package com.example.rivertech.game;

import java.math.BigDecimal;

public class CrazyHouseLogic implements GameLogic {
    public BigDecimal calculateWinnings(int randomNumber, int chosenNumber, BigDecimal betAmount) {
        int difference = Math.abs(randomNumber - chosenNumber);
        if (difference == 0) {
            return betAmount.multiply(BigDecimal.valueOf(20));
        } else {
            return BigDecimal.ZERO;
            }
        }
}
