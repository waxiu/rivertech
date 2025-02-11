package com.example.sportbet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private BigDecimal balance;

    private BigDecimal totalWinnings;

    public Wallet(BigDecimal balance, BigDecimal totalWinnings) {
        this.balance = balance;
        this.totalWinnings = totalWinnings;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Transaction> transactions;

    public void deductBetAmount(BigDecimal betAmount) {
        this.balance = this.balance.subtract(betAmount);
    }

    public void addWinnings(BigDecimal winnings) {
        this.balance = this.balance.add(winnings);
    }
}
