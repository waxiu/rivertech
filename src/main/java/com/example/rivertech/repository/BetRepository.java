package com.example.rivertech.repository;

import com.example.rivertech.model.Bet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findAllByPlayerId(long playerId); // Pobiera zakłady gracza
        Page<Bet> findByPlayerId(long playerId, Pageable pageable);

}
