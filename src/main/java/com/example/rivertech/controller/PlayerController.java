package com.example.rivertech.controller;

import com.example.rivertech.dto.PlayerRegistrationDto;
import com.example.rivertech.model.Player;
import com.example.rivertech.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/register")
    public ResponseEntity<Player> registerPlayer(@RequestBody PlayerRegistrationDto dto) {
        Player registeredPlayer = playerService.registerPlayer(dto);
        return ResponseEntity.ok(registeredPlayer);
    }
}
