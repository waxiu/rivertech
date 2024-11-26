package com.example.rivertech.game;

import com.example.rivertech.game.enums.GameType;
import org.springframework.stereotype.Component;

@Component
public class GameLogicFactory {
    public GameLogic getGameLogic(GameType gameType) {
        return switch (gameType) {
            case ODDS_BASED -> new SimpleGameLogic();
            // Przykład: Można dodać inne rodzaje logiki w przyszłości
            //return new AdvancedGameLogic();
            default -> throw new IllegalArgumentException("Unknown game type: " + gameType);
        };
    }
}
