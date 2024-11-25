package com.example.rivertech.repository;

import com.example.rivertech.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByPlayerId(long playerId); // Pobiera zakłady gracza
}
