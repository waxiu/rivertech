package com.example.rivertech;

import com.example.rivertech.model.Transaction;
import com.example.rivertech.model.Wallet;
import com.example.rivertech.model.enums.TransactionType;
import com.example.rivertech.repository.PlayerRepository;
import com.example.rivertech.repository.TransactionRepository;
import com.example.rivertech.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldFetchTransactionsForPlayer() {
        // given
        Long playerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Transaction transaction = Transaction.builder()
                .type(TransactionType.BET)
                .amount(new BigDecimal("-100"))
                .build();
        Page<Transaction> transactions = new PageImpl<>(List.of(transaction));

        when(playerRepository.existsById(playerId)).thenReturn(true);
        when(transactionRepository.findByPlayerId(eq(playerId), eq(pageable))).thenReturn(transactions);

        // when
        Page<Transaction> result = transactionService.getTransactionsForPlayer(playerId, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getType()).isEqualTo(TransactionType.BET);
        verify(playerRepository).existsById(playerId);
        verify(transactionRepository).findByPlayerId(playerId, pageable);
    }

    @Test
    void shouldThrowExceptionWhenPlayerNotFound() {
        // given
        Long playerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(playerRepository.existsById(playerId)).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> transactionService.getTransactionsForPlayer(playerId, pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player with ID " + playerId + " not found");
        verify(playerRepository).existsById(playerId);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldCreateBetTransaction() {
        // given
        Wallet wallet = new Wallet(new BigDecimal("500"), new BigDecimal("0"));
        wallet.setId(1L);
        BigDecimal betAmount = new BigDecimal("100");

        Transaction betTransaction = Transaction.builder()
                .type(TransactionType.BET)
                .amount(betAmount.negate())
                .wallet(wallet)
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(betTransaction);

        // when
        Transaction result = transactionService.createBetTransaction(wallet, betAmount);

        // then
        assertThat(result.getType()).isEqualTo(TransactionType.BET);
        assertThat(result.getAmount()).isEqualTo(betAmount.negate());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldUpdateWalletWithWinnings() {
        // given
        Wallet wallet = new Wallet(new BigDecimal("500"), new BigDecimal("0"));
        wallet.setId(1L);
        BigDecimal winnings = new BigDecimal("200");

        Transaction winTransaction = Transaction.builder()
                .type(TransactionType.WIN)
                .amount(winnings)
                .wallet(wallet)
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(winTransaction);

        // when
        transactionService.updateWalletAndTransactions(wallet, winnings);

        // then
        verify(transactionRepository).save(any(Transaction.class));
        assertThat(winTransaction.getType()).isEqualTo(TransactionType.WIN);
        assertThat(winTransaction.getAmount()).isEqualTo(winnings);
    }

    @Test
    void shouldNotUpdateWalletWhenNoWinnings() {
        // given
        Wallet wallet = new Wallet(new BigDecimal("500"), new BigDecimal("0"));
        wallet.setId(1L);
        BigDecimal winnings = BigDecimal.ZERO;

        // when
        transactionService.updateWalletAndTransactions(wallet, winnings);

        // then
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
