package com.example.sportbet;

import com.example.sportbet.model.User;
import com.example.sportbet.model.Wallet;
import com.example.sportbet.repository.WalletRepository;
import com.example.sportbet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void shouldCreateWalletForUser() {
        // given
        User user = new User();
        user.setId(1L);
        Wallet wallet = new Wallet(new BigDecimal(1000),new BigDecimal("0"));
        wallet.setUser(user);

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // when
        Wallet result = walletService.createWalletForUser(user);

        // then
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getBalance()).isEqualTo(new BigDecimal(1000));
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void shouldDeductFundsFromWallet() {
        // given
        Wallet wallet = new Wallet(new BigDecimal(1000),new BigDecimal("0"));
        wallet.setId(1L);
        wallet.setTotalWinnings(new BigDecimal(500));
        BigDecimal betAmount = new BigDecimal(100);

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // when
        walletService.deductFundsFromWallet(wallet, betAmount);

        // then
        assertThat(wallet.getBalance()).isEqualTo(new BigDecimal(900));
        assertThat(wallet.getTotalWinnings()).isEqualTo(new BigDecimal(400));
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldAddFundsToWallet() {
        // given
        Wallet wallet = new Wallet(new BigDecimal(1000),new BigDecimal("0"));
        wallet.setId(1L);
        wallet.setTotalWinnings(new BigDecimal(500));
        BigDecimal winnings = new BigDecimal(200);

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // when
        walletService.addFundsToWallet(wallet, winnings);

        // then
        assertThat(wallet.getBalance()).isEqualTo(new BigDecimal(1200));
        assertThat(wallet.getTotalWinnings()).isEqualTo(new BigDecimal(700));
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldDepositFunds() {
        // given
        Wallet wallet = new Wallet(new BigDecimal(1000),new BigDecimal("0"));
        wallet.setId(1L);
        BigDecimal depositAmount = new BigDecimal(300);

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // when
        walletService.depositFunds(wallet, depositAmount);

        // then
        assertThat(wallet.getBalance()).isEqualTo(new BigDecimal(1300));
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldThrowExceptionWhenDepositAmountIsInvalid() {
        // given
        Wallet wallet = new Wallet(new BigDecimal(1000),new BigDecimal("0"));
        wallet.setId(1L);
        BigDecimal invalidDepositAmount = new BigDecimal(-100);

        // when / then
        assertThatThrownBy(() -> walletService.depositFunds(wallet, invalidDepositAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Deposit amount must be greater than zero");
        verifyNoInteractions(walletRepository);
    }
}
