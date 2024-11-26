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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class GameService {

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

        Player player = validatePlayerAndBalance(playerId, betAmount);

        Bet bet = betService.createPendingBet(player, betAmount, chosenNumber);

        walletService.deductFundsFromWallet(player.getWallet(), betAmount);

        transactionService.createBetTransaction(player.getWallet(), betAmount);

        GameResult gameResult = generateGameResult(gameType, bet, betAmount, chosenNumber);

        walletService.addWinningsToWallet(player.getWallet(), gameResult.getWinnings());

        transactionService.updateWalletAndTransactions(player.getWallet(), gameResult.getWinnings());

        betService.finalizeBet(bet, gameResult);

        return gameResult;
    }

    private Player validatePlayerAndBalance(Long playerId, BigDecimal betAmount) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        Wallet wallet = player.getWallet();

        if (wallet.getBalance().compareTo(betAmount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        return player;
    }

    private GameResult generateGameResult(GameType gameType, Bet bet, BigDecimal betAmount, int chosenNumber) {
        int randomNumber = generateRandomNumber();
        GameLogic gameLogic = gameLogicFactory.getGameLogic(gameType);
        BigDecimal winnings = gameLogic.calculateWinnings(randomNumber, chosenNumber, betAmount);

        bet.setGeneratedNumber(randomNumber);
        bet.setWinnings(winnings);
        return new GameResult(randomNumber, winnings);
    }

    private int generateRandomNumber() {
        return new Random().nextInt(11);
    }
}
