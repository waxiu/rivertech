package com.example.sportbet.game;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class CrazyHouseLogic implements GameLogic {

    @Override
    public BigDecimal calculateWinnings(int randomNumber, int chosenNumber, BigDecimal betAmount) {
        int difference = Math.abs(randomNumber - chosenNumber);
        if (difference == 0) {
            return betAmount.multiply(BigDecimal.valueOf(20));
        } else {
            return BigDecimal.ZERO;
        }
    }
}
