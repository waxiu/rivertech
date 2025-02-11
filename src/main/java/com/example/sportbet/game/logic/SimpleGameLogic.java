package com.example.sportbet.game.logic;

import java.math.BigDecimal;

import com.example.sportbet.game.logic.GameLogic;
import org.springframework.stereotype.Component;

@Component
public class SimpleGameLogic implements GameLogic {

    @Override
    public BigDecimal calculateWinnings(int randomNumber, int chosenNumber, BigDecimal betAmount) {
        int difference = Math.abs(randomNumber - chosenNumber);
        if (difference == 0) {
            return betAmount.multiply(BigDecimal.valueOf(10));
        } else if (difference == 1) {
            return betAmount.multiply(BigDecimal.valueOf(5));
        } else if (difference == 2) {
            return betAmount.divide(BigDecimal.valueOf(2));
        } else {
            return BigDecimal.ZERO;
        }
    }
}
