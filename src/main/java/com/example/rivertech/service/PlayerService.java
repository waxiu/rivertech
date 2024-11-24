package com.example.rivertech.service;

import com.example.rivertech.dto.PlayerRegistrationDto;
import com.example.rivertech.model.Player;
import com.example.rivertech.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player registerPlayer(PlayerRegistrationDto dto) {
        if (playerRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }
        Player player = new Player(dto.getName(), dto.getSurname(), dto.getUsername());

        player.getWallet().setBalance(new BigDecimal(1000));

        return playerRepository.save(player);
    }
}
