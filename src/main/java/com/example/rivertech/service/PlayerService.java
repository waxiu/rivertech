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
    private final WalletRepository walletRepository;

    public PlayerService(PlayerRepository playerRepository, WalletRepository walletRepository) {
        this.playerRepository = playerRepository;
        this.walletRepository = walletRepository;
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

        Wallet wallet = new Wallet(BigDecimal.valueOf(1000.00));
        wallet.setPlayer(savedPlayer);

        walletRepository.save(wallet);
        logger.info("Wallet created with initial balance: {} for playerId: {}", wallet.getBalance(), savedPlayer.getId());

        return savedPlayer;
    }
}
