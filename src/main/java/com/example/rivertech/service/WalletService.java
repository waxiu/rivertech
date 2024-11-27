package com.example.rivertech.service;

import com.example.rivertech.model.Wallet;
import com.example.rivertech.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }
    public void deductFundsFromWallet(Wallet wallet, BigDecimal betAmount) {
        wallet.deductBetAmount(betAmount);
        wallet.setTotalWinnings(wallet.getTotalWinnings().subtract(betAmount));
        walletRepository.save(wallet);
    }

    public void addFundsToWallet(Wallet wallet, BigDecimal winnings) {
        wallet.addWinnings(winnings);
        wallet.setTotalWinnings(wallet.getTotalWinnings().add(winnings));
        walletRepository.save(wallet);
    }
}
