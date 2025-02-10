package com.example.rivertech;

import com.example.rivertech.dto.GameResultDto;
import com.example.rivertech.game.GameLogic;
import com.example.rivertech.game.GameLogicFactory;
import com.example.rivertech.game.enums.GameType;
import com.example.rivertech.model.*;
import com.example.rivertech.repository.UserRepository;
import com.example.rivertech.repository.UserRepository;
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
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private BetService betService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private GameService gameService;

    @Test
    void playGame_shouldDeductFundsAndAddWinnings_whenUserWins() {
        // Arrange
        Long userId = 1L;
        BigDecimal betAmount = new BigDecimal("100");
        int chosenNumber = 5;
        GameType gameType = GameType.ODDS_BASED;
        int generatedNumber = 5; // Oczekiwana losowa liczba
        BigDecimal winnings = new BigDecimal("1000");

        User user = new User();
        Wallet wallet = new Wallet(new BigDecimal("1000"),new BigDecimal("0"));
        user.setWallet(wallet);

        Transaction transaction = new Transaction();
        Bet bet = new Bet();
        GameLogic gameLogic = mock(GameLogic.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameLogicFactory.getGameLogic(gameType)).thenReturn(gameLogic);
        when(gameLogic.calculateWinnings(generatedNumber, chosenNumber, betAmount)).thenReturn(winnings);
        when(transactionService.createBetTransaction(wallet, betAmount)).thenReturn(transaction);
        when(betService.createPendingBet(user, betAmount, chosenNumber, transaction)).thenReturn(bet);

        // Mockowanie losowej liczby
        GameService gameServiceSpy = spy(gameService);
        doReturn(generatedNumber).when(gameServiceSpy).generateRandomNumber();

        // Act
        GameResultDto result = gameServiceSpy.playGame(userId, betAmount, chosenNumber, gameType);

        // Assert
        assertThat(result.getGeneratedNumber()).isEqualTo(generatedNumber);
        assertThat(result.getWinnings()).isEqualByComparingTo(winnings);

        verify(walletService).deductFundsFromWallet(wallet, betAmount);
        verify(walletService).addFundsToWallet(wallet, winnings);
        verify(transactionService).updateWalletAndTransactions(wallet, winnings);
        verify(betService).finalizeBet(bet, result);
    }

    @Test
    void playGame_shouldThrowException_whenUserNotFound() {
        // Arrange
        Long userId = 1L;
        BigDecimal betAmount = new BigDecimal("100");
        int chosenNumber = 5;
        GameType gameType = GameType.ODDS_BASED;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> gameService.playGame(userId, betAmount, chosenNumber, gameType))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verifyNoInteractions(walletService, transactionService, betService);
    }

    @Test
    void playGame_shouldThrowException_whenInsufficientFunds() {
        // Arrange
        Long userId = 1L;
        BigDecimal betAmount = new BigDecimal("1000");
        User user = new User();
        Wallet wallet = new Wallet(new BigDecimal("500"),new BigDecimal("0"));
        user.setWallet(wallet);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> gameService.playGame(userId, betAmount, 5, GameType.ODDS_BASED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Insufficient funds");

        verifyNoInteractions(walletService, transactionService, betService);
    }
}
