package com.example.rivertech.service;

import com.example.rivertech.model.Bet;
import com.example.rivertech.repository.BetRepository;
import org.springframework.stereotype.Service;

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
}
