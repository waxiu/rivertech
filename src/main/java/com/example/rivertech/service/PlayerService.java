package com.example.rivertech.service;

import com.example.rivertech.dto.PlayerRegistrationDto;
import com.example.rivertech.model.Player;
import com.example.rivertech.model.Wallet;
import com.example.rivertech.repository.PlayerRepository;
import com.example.rivertech.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PlayerService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    private final WalletService walletService;

    public PlayerService(PlayerRepository playerRepository, WalletService walletService) {
        this.playerRepository = playerRepository;
        this.walletService = walletService;
    }

    public Player registerPlayer(PlayerRegistrationDto dto) {
        logger.info("Registering new player with username: {}", dto.getUsername());

        Player player = Player.builder()
                .name(dto.getName())
                .surname(dto.getSurname())
                .username(dto.getUsername())
                .build();

        Player savedPlayer = playerRepository.save(player);
        logger.info("Player registered successfully with playerId: {}", savedPlayer.getId());

        walletService.createWalletForPlayer(player);

        return savedPlayer;
    }

    public void depositToPlayerWallet(Long playerId, BigDecimal amount) {
        logger.info("Starting deposit for playerId: {} with amount: {}", playerId, amount);
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> {
                    logger.error("Player with ID {} not found", playerId);
                    return new IllegalArgumentException("Player not found with ID: " + playerId);
                });
        Wallet wallet = player.getWallet();
        walletService.depositFunds(wallet, amount);
        logger.info("Deposit to playerId: {} completed successfully", playerId);
    }
}
