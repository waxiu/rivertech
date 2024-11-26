package com.example.rivertech.service;

import com.example.rivertech.dto.GameResult;
import com.example.rivertech.model.Bet;
import com.example.rivertech.model.Player;
import com.example.rivertech.model.enums.BetStatus;
import com.example.rivertech.repository.BetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BetService {

    private final BetRepository betRepository;

    public BetService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    public List<Bet> getBetsForPlayer(long playerId) {
        return betRepository.findByPlayerId(playerId);
    }

    public Bet createPendingBet(Player player, BigDecimal betAmount, int chosenNumber) {
        Bet bet = Bet.builder()
                .player(player)
                .betAmount(betAmount)
                .betNumber(chosenNumber)
                .status(BetStatus.PENDING)
                .build();
        return betRepository.save(bet);
    }

    public void finalizeBet(Bet bet, GameResult gameResult) {
        bet.setStatus(BetStatus.COMPLETED);
        bet.setGeneratedNumber(gameResult.getGeneratedNumber());
        bet.setWinnings(gameResult.getWinnings());
        betRepository.save(bet);
    }
}
