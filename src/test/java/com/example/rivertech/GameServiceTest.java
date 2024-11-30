package com.example.rivertech;

import com.example.rivertech.dto.GameResultDto;
import com.example.rivertech.game.GameLogic;
import com.example.rivertech.game.GameLogicFactory;
import com.example.rivertech.game.enums.GameType;
import com.example.rivertech.model.*;
import com.example.rivertech.repository.PlayerRepository;
import com.example.rivertech.service.BetService;
import com.example.rivertech.service.GameService;
import com.example.rivertech.service.TransactionService;
import com.example.rivertech.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameLogicFactory gameLogicFactory;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private BetService betService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private GameService gameService;

    @Test
    void playGame_shouldDeductFundsAndAddWinnings_whenPlayerWins() {
        // Arrange
        Long playerId = 1L;
        BigDecimal betAmount = new BigDecimal("100");
        int chosenNumber = 5;
        GameType gameType = GameType.ODDS_BASED;
        int generatedNumber = 5; // Oczekiwana losowa liczba
        BigDecimal winnings = new BigDecimal("1000");

        Player player = new Player();
        Wallet wallet = new Wallet(new BigDecimal("1000"));
        player.setWallet(wallet);

        Transaction transaction = new Transaction();
        Bet bet = new Bet();
        GameLogic gameLogic = mock(GameLogic.class);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(gameLogicFactory.getGameLogic(gameType)).thenReturn(gameLogic);
        when(gameLogic.calculateWinnings(generatedNumber, chosenNumber, betAmount)).thenReturn(winnings);
        when(transactionService.createBetTransaction(wallet, betAmount)).thenReturn(transaction);
        when(betService.createPendingBet(player, betAmount, chosenNumber, transaction)).thenReturn(bet);

        // Mockowanie losowej liczby
        GameService gameServiceSpy = spy(gameService);
        doReturn(generatedNumber).when(gameServiceSpy).generateRandomNumber();

        // Act
        GameResultDto result = gameServiceSpy.playGame(playerId, betAmount, chosenNumber, gameType);

        // Assert
        assertThat(result.getGeneratedNumber()).isEqualTo(generatedNumber);
        assertThat(result.getWinnings()).isEqualByComparingTo(winnings);

        verify(walletService).deductFundsFromWallet(wallet, betAmount);
        verify(walletService).addFundsToWallet(wallet, winnings);
        verify(transactionService).updateWalletAndTransactions(wallet, winnings);
        verify(betService).finalizeBet(bet, result);
    }

    @Test
    void playGame_shouldThrowException_whenPlayerNotFound() {
        // Arrange
        Long playerId = 1L;
        BigDecimal betAmount = new BigDecimal("100");
        int chosenNumber = 5;
        GameType gameType = GameType.ODDS_BASED;

        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> gameService.playGame(playerId, betAmount, chosenNumber, gameType))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Player not found");

        verifyNoInteractions(walletService, transactionService, betService);
    }

    @Test
    void playGame_shouldThrowException_whenInsufficientFunds() {
        // Arrange
        Long playerId = 1L;
        BigDecimal betAmount = new BigDecimal("1000");
        Player player = new Player();
        Wallet wallet = new Wallet(new BigDecimal("500"));
        player.setWallet(wallet);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        // Act & Assert
        assertThatThrownBy(() -> gameService.playGame(playerId, betAmount, 5, GameType.ODDS_BASED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Insufficient funds");

        verifyNoInteractions(walletService, transactionService, betService);
    }
}
