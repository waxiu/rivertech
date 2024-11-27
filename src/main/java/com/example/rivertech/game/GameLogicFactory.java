package com.example.rivertech.game;

import com.example.rivertech.game.enums.GameType;
import org.springframework.stereotype.Component;

@Component
public class GameLogicFactory {
    public GameLogic getGameLogic(GameType gameType) {
        return switch (gameType) {
            case ODDS_BASED -> new SimpleGameLogic();
            case CRAZY_HOUSE -> new CrazyHouseLogic();
        };
    }
}
