package com.example.sportbet.repository;

import com.example.sportbet.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        JOIN w.user p
        WHERE p.id = :userId
       """)
    Page<Transaction> findByUserId(@Param("userId") Long userId, Pageable pageable);
    }
