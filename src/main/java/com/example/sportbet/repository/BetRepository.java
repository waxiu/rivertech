package com.example.sportbet.repository;

import com.example.sportbet.model.Bet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findAllByUserId(long userId);
    Page<Bet> findByUserId(long userId, Pageable pageable);

}
