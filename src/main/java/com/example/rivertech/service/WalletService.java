package com.example.rivertech.service;

import com.example.rivertech.model.Player;
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

    public Wallet createWalletForPlayer(Player player) {
        Wallet wallet = new Wallet(new BigDecimal(1000));
        wallet.setPlayer(player);
        Wallet savedWallet = walletRepository.save(wallet);
        logger.info("Wallet created with initial balance: {} for playerId: {}", wallet.getBalance(), player.getId());
        return savedWallet;
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

    public void depositFunds(Wallet wallet, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
        logger.info("Depositing amount: {} to walletId: {}", amount, wallet.getId());
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        logger.info("Deposit successful. New balance for walletId: {} is {}", wallet.getId(), wallet.getBalance());
    }
}
