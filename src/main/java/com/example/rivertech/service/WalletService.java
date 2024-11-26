package com.example.rivertech.service;

import com.example.rivertech.model.Wallet;
import com.example.rivertech.repository.BetRepository;
import com.example.rivertech.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {
    private WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }
    public void deductFundsFromWallet(Wallet wallet, BigDecimal betAmount) {
        wallet.deductBetAmount(betAmount);
        walletRepository.save(wallet);
    }

    public void addWinningsToWallet(Wallet wallet, BigDecimal winnings) {
        wallet.addWinnings(winnings);
        walletRepository.save(wallet);
    }


}
