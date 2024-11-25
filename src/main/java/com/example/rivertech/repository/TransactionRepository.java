package com.example.rivertech.repository;

import com.example.rivertech.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""

            SELECT t FROM Transaction t
           JOIN t.wallet w
           JOIN w.player p
           WHERE p.id = :playerId
           """)
    List<Transaction> findByPlayerId(@Param("playerId") Long playerId);
    }
