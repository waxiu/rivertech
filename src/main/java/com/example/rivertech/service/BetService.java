package com.example.rivertech.service;

import com.example.rivertech.dto.BetHistory;
import com.example.rivertech.dto.GameResult;
import com.example.rivertech.model.Bet;
import com.example.rivertech.model.Player;
import com.example.rivertech.model.Transaction;
import com.example.rivertech.model.enums.BetStatus;
import com.example.rivertech.repository.BetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BetService {

    private static final Logger logger = LoggerFactory.getLogger(BetService.class);

    private final BetRepository betRepository;

    public BetService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    public List<Bet> getBetsForPlayer(long playerId) {
        logger.info("Retrieving bets for playerId: {}", playerId);
        List<Bet> bets = betRepository.findAllByPlayerId(playerId);
        logger.debug("Found {} bets for playerId: {}", bets.size(), playerId);
        return bets;
    }

    public Bet createPendingBet(Player player, BigDecimal betAmount, int chosenNumber, Transaction transaction) {
        logger.info("Creating pending bet for playerId: {}, betAmount: {}, chosenNumber: {}",
                player.getId(), betAmount, chosenNumber);
        Bet bet = Bet.builder()
                .player(player)
                .betAmount(betAmount)
                .betNumber(chosenNumber)
                .status(BetStatus.PENDING)
                .transaction(transaction)
                .build();
        Bet savedBet = betRepository.save(bet);
        logger.info("Pending bet created with betId: {}, playerId: {}", savedBet.getId(), player.getId());
        return savedBet;
    }

    public void finalizeBet(Bet bet, GameResult gameResult) {
        logger.info("Finalizing bet with betId: {}, for playerId: {}", bet.getId(), bet.getPlayer().getId());
        logger.debug("Updating bet with generatedNumber: {}, winnings: {}",
                gameResult.getGeneratedNumber(), gameResult.getWinnings());
        bet.setStatus(BetStatus.COMPLETED);
        bet.setGeneratedNumber(gameResult.getGeneratedNumber());
        bet.setWinnings(gameResult.getWinnings());
        betRepository.save(bet);
        logger.info("Bet finalized with betId: {}, status: {}, winnings: {}",
                bet.getId(), bet.getStatus(), bet.getWinnings());
    }

    public Page<BetHistory> getBetHistoryForPlayer(long playerId, Pageable pageable) {
        logger.info("Fetching paginated bet history for playerId: {}, page: {}, size: {}",
                playerId, pageable.getPageNumber(), pageable.getPageSize());

        return betRepository.findByPlayerId(playerId, pageable)
                .map(bet -> new BetHistory(
                        bet.getBetAmount(),
                        bet.getBetNumber(),
                        bet.getGeneratedNumber(),
                        bet.getWinnings(),
                        bet.getStatus()));
    }
}
