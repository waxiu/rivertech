package com.example.rivertech.game;

import com.example.rivertech.game.enums.GameType;
import org.springframework.stereotype.Component;

@Component
public class GameLogicFactory {

    private final SimpleGameLogic simpleGameLogic;
    private final CrazyHouseLogic crazyHouseLogic;

    public GameLogicFactory(SimpleGameLogic simpleGameLogic, CrazyHouseLogic crazyHouseLogic) {
        this.simpleGameLogic = simpleGameLogic;
        this.crazyHouseLogic = crazyHouseLogic;
    }

    public GameLogic getGameLogic(GameType gameType) {
        return switch (gameType) {
            case ODDS_BASED -> simpleGameLogic;
            case CRAZY_HOUSE -> crazyHouseLogic;
        };
    }
}
