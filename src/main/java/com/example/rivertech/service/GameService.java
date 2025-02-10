package com.example.rivertech.service;

import com.example.rivertech.dto.GameResultDto;
import com.example.rivertech.game.GameLogic;
import com.example.rivertech.game.GameLogicFactory;
import com.example.rivertech.game.enums.GameType;
import com.example.rivertech.model.Bet;
import com.example.rivertech.model.User;
import com.example.rivertech.model.Transaction;
import com.example.rivertech.model.Wallet;
import com.example.rivertech.model.enums.BetStatus;
import com.example.rivertech.model.enums.TransactionType;
import com.example.rivertech.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final GameLogicFactory gameLogicFactory;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final BetService betService;
    private final TransactionService transactionService;

    public GameService(GameLogicFactory gameLogicFactory,
                       UserRepository userRepository,
                       WalletService walletService,
                       BetService betService,
                       TransactionService transactionService) {
        this.gameLogicFactory = gameLogicFactory;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.betService = betService;
        this.transactionService = transactionService;
    }

    public GameResultDto playGame(Long userId, BigDecimal betAmount, int chosenNumber, GameType gameType) {
        logger.info("Game initiated for userId: {}, betAmount: {}, chosenNumber: {}, gameType: {}",
                userId, betAmount, chosenNumber, gameType);

        User user = validateUserAndBalance(userId, betAmount);

        walletService.deductFundsFromWallet(user.getWallet(), betAmount);
        logger.info("Funds deducted from walletId: {} for amount: {}", user.getWallet().getId(), betAmount);

        Transaction transaction = transactionService.createBetTransaction(user.getWallet(), betAmount);
        logger.info("Transaction created for walletId: {}, amount: {}, type: {}",
                user.getWallet().getId(), betAmount, TransactionType.BET);

        Bet bet = betService.createPendingBet(user, betAmount, chosenNumber, transaction);
        logger.info("Bet created with betId: {} for userId: {}", bet.getId(), userId);

        // Generate random number once
        int randomNumber = generateRandomNumber();

        GameResultDto gameResultDto = generateGameResult(gameType, bet, betAmount, chosenNumber, randomNumber);
        logger.info("Game result generated with randomNumber: {}, winnings: {}",
                gameResultDto.getGeneratedNumber(), gameResultDto.getWinnings());

        walletService.addFundsToWallet(user.getWallet(), gameResultDto.getWinnings());
        logger.info("Funds added to walletId: {} for winnings: {}", user.getWallet().getId(), gameResultDto.getWinnings());

        transactionService.updateWalletAndTransactions(user.getWallet(), gameResultDto.getWinnings());
        logger.info("Transactions updated for walletId: {} with winnings: {}",
                user.getWallet().getId(), gameResultDto.getWinnings());

        betService.finalizeBet(bet, gameResultDto);
        logger.info("Bet finalized with betId: {}, status: {}, winnings: {}",
                bet.getId(), BetStatus.COMPLETED, bet.getWinnings());

        logger.info("Game completed for userId: {}, betId: {}", user, bet.getId());
        return gameResultDto;
    }

    private User validateUserAndBalance(Long userId, BigDecimal betAmount) {
        logger.debug("Validating userId: {} and balance for betAmount: {}", userId, betAmount);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with userId: {} not found", userId);
                    return new RuntimeException("User not found");
                });
        Wallet wallet = user.getWallet();

        if (wallet.getBalance().compareTo(betAmount) < 0) {
            logger.error("Insufficient funds for userId: {}, walletId: {}, balance: {}, betAmount: {}",
                    userId, wallet.getId(), wallet.getBalance(), betAmount);
            throw new RuntimeException("Insufficient funds");
        }
        logger.debug("User validated with userId: {} and sufficient balance", userId);
        return user;
    }

    private GameResultDto generateGameResult(GameType gameType, Bet bet, BigDecimal betAmount, int chosenNumber, int randomNumber) {
        logger.debug("Generating game result for betId: {}, gameType: {}", bet.getId(), gameType);
        GameLogic gameLogic = gameLogicFactory.getGameLogic(gameType);
        BigDecimal winnings = gameLogic.calculateWinnings(randomNumber, chosenNumber, betAmount);

        bet.setGeneratedNumber(randomNumber);
        bet.setWinnings(winnings);
        logger.debug("Game result generated for betId: {}, randomNumber: {}, winnings: {}",
                bet.getId(), randomNumber, winnings);
        return new GameResultDto(randomNumber, winnings);
    }


    public int generateRandomNumber() {
        int randomNumber = new Random().nextInt(10) + 1;
        logger.debug("Generated random number: {}", randomNumber);
        return randomNumber;
    }
}
