package com.example.rivertech.game;

import org.springframework.stereotype.Component;

@Component
public class GameLogicFactory {
    public GameLogic getGameLogic(String gameType) {
        return switch (gameType.toLowerCase()) {
            case "simple" -> new SimpleGameLogic();
            // Przykład: Można dodać inne rodzaje logiki w przyszłości
            //return new AdvancedGameLogic();
            default -> throw new IllegalArgumentException("Unknown game type: " + gameType);
        };
    }
}
