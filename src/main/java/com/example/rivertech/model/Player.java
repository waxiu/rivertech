package com.example.rivertech.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String surname;
    @Column(unique = true, nullable = false)
    private String username;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Wallet wallet;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionHistory> transactionHistory;

    public Player(String name, String surname, String username) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.wallet = new Wallet();
    }
}
