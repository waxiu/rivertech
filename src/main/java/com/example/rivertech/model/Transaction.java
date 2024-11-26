package com.example.rivertech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String type; // Np. "BET", "WIN", "DEPOSIT"

    private BigDecimal amount; // Kwota transakcji

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date(); // Data utworzenia transakcji

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    @JsonIgnore
    private Wallet wallet;
}
