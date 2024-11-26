package com.example.rivertech.game;

import java.math.BigDecimal;

public interface GameLogic {
    BigDecimal calculateWinnings(int randomNumber, int chosenNumber, BigDecimal betAmount);
}
