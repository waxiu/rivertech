package com.example.rivertech.service;

import com.example.rivertech.model.Wallet;
import com.example.rivertech.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void deductFundsFromWallet(Wallet wallet, BigDecimal betAmount) {
        logger.info("Deducting funds from walletId: {}, betAmount: {}", wallet.getId(), betAmount);

        wallet.deductBetAmount(betAmount);
        wallet.setTotalWinnings(wallet.getTotalWinnings().subtract(betAmount));
        walletRepository.save(wallet);

        logger.info("Funds deducted successfully. WalletId: {}, new balance: {}, total winnings: {}",
                wallet.getId(), wallet.getBalance(), wallet.getTotalWinnings());
    }

    public void addFundsToWallet(Wallet wallet, BigDecimal winnings) {
        logger.info("Adding winnings to walletId: {}, amount: {}", wallet.getId(), winnings);

        wallet.addWinnings(winnings);
        wallet.setTotalWinnings(wallet.getTotalWinnings().add(winnings));
        walletRepository.save(wallet);

        logger.info("Winnings added successfully. WalletId: {}, new balance: {}, total winnings: {}",
                wallet.getId(), wallet.getBalance(), wallet.getTotalWinnings());
    }
}
