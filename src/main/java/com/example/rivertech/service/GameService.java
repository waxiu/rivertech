package com.example.rivertech.service;

import com.example.rivertech.dto.GameResult;
import com.example.rivertech.game.GameLogic;
import com.example.rivertech.game.GameLogicFactory;
import com.example.rivertech.model.Bet;
import com.example.rivertech.model.Player;
import com.example.rivertech.model.Transaction;
import com.example.rivertech.model.Wallet;
import com.example.rivertech.repository.BetRepository;
import com.example.rivertech.repository.PlayerRepository;
import com.example.rivertech.repository.TransactionRepository;
import com.example.rivertech.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class GameService {

    private final GameLogicFactory gameLogicFactory;
    private final PlayerRepository playerRepository;
    private final WalletRepository walletRepository;
    private final BetRepository betRepository;

    public GameService(GameLogicFactory gameLogicFactory,
                       PlayerRepository playerRepository,
                       WalletRepository walletRepository,
                       BetRepository betRepository) {
        this.gameLogicFactory = gameLogicFactory;
        this.playerRepository = playerRepository;
        this.walletRepository = walletRepository;
        this.betRepository = betRepository;
    }

    public GameResult playGame(Long playerId, BigDecimal betAmount, int chosenNumber, String gameType) {
        // Pobranie gracza i jego portfela
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        Wallet wallet = player.getWallet();

        // Walidacja salda
        if (wallet.getBalance().compareTo(betAmount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // Generacja losowej liczby i obliczenie wygranej
        int randomNumber = generateRandomNumber();
        GameLogic gameLogic = gameLogicFactory.getGameLogic(gameType);
        BigDecimal winnings = gameLogic.calculateWinnings(randomNumber, chosenNumber, betAmount);

        // Aktualizacja salda
        wallet.updateBalance(betAmount,winnings);
        walletRepository.save(wallet);

        // Tworzenie transakcji dla zakładu i wygranej
        Transaction betTransaction = Transaction.builder()
                .type("BET")
                .amount(betAmount.negate()) // Kwota zakładu jako ujemna
                .wallet(wallet)
                .build();

        Transaction winTransaction = null;
        if (winnings.compareTo(BigDecimal.ZERO) > 0) {
            winTransaction = Transaction.builder()
                    .type("WIN")
                    .amount(winnings)
                    .wallet(wallet)
                    .build();
        }

        // Tworzenie zakładu i powiązanie go z transakcjami
        Bet bet = Bet.builder()
                .betAmount(betAmount)
                .betNumber(chosenNumber)
                .generatedNumber(randomNumber)
                .winnings(winnings)
                .player(player)
                .transaction(betTransaction) // Powiązanie z transakcją zakładu
                .build();

        // Zapis zakładu i transakcji
        betRepository.save(bet);
        if (winTransaction != null) {
            wallet.getTransactions().add(winTransaction);
        }

        return new GameResult(randomNumber, winnings);
    }

    private int generateRandomNumber() {
        return new Random().nextInt(10) + 1;
    }
}
