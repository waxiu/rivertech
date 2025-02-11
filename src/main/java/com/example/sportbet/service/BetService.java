package com.example.sportbet.service;

import com.example.sportbet.dto.response.BetHistoryResponseDto;
import com.example.sportbet.dto.response.GameResultResponseDto;
import com.example.sportbet.model.Bet;
import com.example.sportbet.model.User;
import com.example.sportbet.model.Transaction;
import com.example.sportbet.model.enums.BetStatus;
import com.example.sportbet.repository.BetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BetService {

    private static final Logger logger = LoggerFactory.getLogger(BetService.class);
    private final BetRepository betRepository;

    public BetService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    public Page<Bet> getBetsForUser(long userId, Pageable pageable) {
        logger.info("Retrieving bets for UserId: {}", userId);
        Page<Bet> bets = betRepository.findByUserId(userId, pageable);
        logger.debug("Fetching paginated bets for userId: {}, page: {}, size: {}", userId, pageable.getPageNumber(),pageable.getPageSize());
        return bets;
    }

    @Transactional
    public Bet createPendingBet(User user, BigDecimal betAmount, int chosenNumber, Transaction transaction) {
        if (betAmount == null || betAmount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Invalid bet amount: {}", betAmount);
            throw new IllegalArgumentException("Bet amount must be greater than zero.");
        }

        logger.info("Creating pending bet for userId: {}, betAmount: {}, chosenNumber: {}",
                user.getId(), betAmount, chosenNumber);
        Bet bet = Bet.builder()
                .user(user)
                .betAmount(betAmount)
                .betNumber(chosenNumber)
                .status(BetStatus.PENDING)
                .transaction(transaction)
                .build();
        Bet savedBet = betRepository.save(bet);
        logger.info("Pending bet created with betId: {}, userId: {}", savedBet.getId(), user.getId());
        return savedBet;
    }

    @Transactional
    public void finalizeBet(Bet bet, GameResultResponseDto gameResultResponseDto) {
        logger.info("Finalizing bet with betId: {}, for userId: {}", bet.getId(), bet.getUser().getId());

        if (bet.getStatus() != BetStatus.PENDING) {
            logger.error("Attempted to finalize a non-pending bet with betId: {}", bet.getId());
            throw new IllegalStateException("Bet can only be finalized if it is in a pending state.");
        }

        bet.setStatus(BetStatus.COMPLETED);
        bet.setGeneratedNumber(gameResultResponseDto.getGeneratedNumber());
        bet.setWinnings(gameResultResponseDto.getWinnings());
        Bet updatedBet = betRepository.save(bet);

        logger.info("Bet finalized with betId: {}, status: {}, winnings: {}",
                updatedBet.getId(), updatedBet.getStatus(), updatedBet.getWinnings());
    }

    public Page<BetHistoryResponseDto> getBetHistoryForUser(long userId, Pageable pageable) {
        logger.info("Fetching paginated bet history for userId: {}, page: {}, size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        return betRepository.findByUserId(userId, pageable)
                .map(bet -> new BetHistoryResponseDto(
                        bet.getBetAmount(),
                        bet.getBetNumber(),
                        bet.getGeneratedNumber(),
                        bet.getWinnings(),
                        bet.getStatus()));
    }
}
