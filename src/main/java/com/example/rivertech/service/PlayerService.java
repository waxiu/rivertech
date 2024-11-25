package com.example.rivertech.service;

import com.example.rivertech.dto.PlayerRegistrationDto;
import com.example.rivertech.model.Player;
import com.example.rivertech.model.Wallet;
import com.example.rivertech.repository.PlayerRepository;
import com.example.rivertech.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final WalletRepository walletRepository;

    public PlayerService(PlayerRepository playerRepository, WalletRepository walletRepository) {
        this.playerRepository = playerRepository;
        this.walletRepository = walletRepository;
    }

    public Player registerPlayer(PlayerRegistrationDto dto) {
        // Utwórz nowego gracza
        Player player = new Player();
        player.setName(dto.getName());
        player.setSurname(dto.getSurname());
        player.setUsername(dto.getUsername());

        // Zapisz gracza w bazie
        Player savedPlayer = playerRepository.save(player);

        // Utwórz portfel z początkowym stanem (np. 1000 kredytów)
        Wallet wallet = new Wallet(BigDecimal.valueOf(1000.00));
        wallet.setPlayer(savedPlayer);

        // Zapisz portfel w bazie
        walletRepository.save(wallet);

        return savedPlayer;
    }
}
