package com.example.rivertech.service;

import com.example.rivertech.dto.GameResult;
import com.example.rivertech.game.GameLogic;
import com.example.rivertech.game.GameLogicFactory;
import com.example.rivertech.game.enums.GameType;
import com.example.rivertech.model.Bet;
import com.example.rivertech.model.Player;
import com.example.rivertech.model.Transaction;
import com.example.rivertech.model.Wallet;
import com.example.rivertech.model.enums.BetStatus;
import com.example.rivertech.model.enums.TransactionType;
import com.example.rivertech.repository.BetRepository;
import com.example.rivertech.repository.PlayerRepository;
import com.example.rivertech.repository.TransactionRepository;
import com.example.rivertech.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final GameLogicFactory gameLogicFactory;
    private final PlayerRepository playerRepository;
    private final WalletService walletService;
    private final BetService betService;
    private final TransactionService transactionService;

    public GameService(GameLogicFactory gameLogicFactory,
                       PlayerRepository playerRepository,
                       WalletService walletService,
                       BetService betService,
                       TransactionService transactionService) {
        this.gameLogicFactory = gameLogicFactory;
        this.playerRepository = playerRepository;
        this.walletService = walletService;
        this.betService = betService;
        this.transactionService = transactionService;
    }

    @Transactional
    public GameResult playGame(Long playerId, BigDecimal betAmount, int chosenNumber, GameType gameType) {
        logger.info("Game initiated for playerId: {}, betAmount: {}, chosenNumber: {}, gameType: {}",
                playerId, betAmount, chosenNumber, gameType);

        Player player = validatePlayerAndBalance(playerId, betAmount);

        Bet bet = betService.createPendingBet(player, betAmount, chosenNumber);
        logger.info("Bet created with betId: {} for playerId: {}", bet.getId(), playerId);

        walletService.deductFundsFromWallet(player.getWallet(), betAmount);
        logger.info("Funds deducted from walletId: {} for amount: {}", player.getWallet().getId(), betAmount);

        transactionService.createBetTransaction(player.getWallet(), betAmount);
        logger.info("Transaction created for walletId: {}, amount: {}, type: {}",
                player.getWallet().getId(), betAmount, TransactionType.BET);

        GameResult gameResult = generateGameResult(gameType, bet, betAmount, chosenNumber);
        logger.info("Game result generated with randomNumber: {}, winnings: {}",
                gameResult.getGeneratedNumber(), gameResult.getWinnings());

        walletService.addFundsToWallet(player.getWallet(), gameResult.getWinnings());
        logger.info("Funds added to walletId: {} for winnings: {}", player.getWallet().getId(), gameResult.getWinnings());

        transactionService.updateWalletAndTransactions(player.getWallet(), gameResult.getWinnings());
        logger.info("Transactions updated for walletId: {} with winnings: {}",
                player.getWallet().getId(), gameResult.getWinnings());

        betService.finalizeBet(bet, gameResult);
        logger.info("Bet finalized with betId: {}, status: {}, winnings: {}",
                bet.getId(), BetStatus.COMPLETED, bet.getWinnings());

        logger.info("Game completed for playerId: {}, betId: {}", playerId, bet.getId());
        return gameResult;
    }

    private Player validatePlayerAndBalance(Long playerId, BigDecimal betAmount) {
        logger.debug("Validating playerId: {} and balance for betAmount: {}", playerId, betAmount);
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> {
                    logger.error("Player with playerId: {} not found", playerId);
                    return new RuntimeException("Player not found");
                });
        Wallet wallet = player.getWallet();

        if (wallet.getBalance().compareTo(betAmount) < 0) {
            logger.error("Insufficient funds for playerId: {}, walletId: {}, balance: {}, betAmount: {}",
                    playerId, wallet.getId(), wallet.getBalance(), betAmount);
            throw new RuntimeException("Insufficient funds");
        }
        logger.debug("Player validated with playerId: {} and sufficient balance", playerId);
        return player;
    }

    private GameResult generateGameResult(GameType gameType, Bet bet, BigDecimal betAmount, int chosenNumber) {
        logger.debug("Generating game result for betId: {}, gameType: {}", bet.getId(), gameType);
        int randomNumber = generateRandomNumber();
        GameLogic gameLogic = gameLogicFactory.getGameLogic(gameType);
        BigDecimal winnings = gameLogic.calculateWinnings(randomNumber, chosenNumber, betAmount);

        bet.setGeneratedNumber(randomNumber);
        bet.setWinnings(winnings);
        logger.debug("Game result generated for betId: {}, randomNumber: {}, winnings: {}",
                bet.getId(), randomNumber, winnings);
        return new GameResult(randomNumber, winnings);
    }

    private int generateRandomNumber() {
        int randomNumber = new Random().nextInt(11);
        logger.debug("Generated random number: {}", randomNumber);
        return randomNumber;
    }
}
