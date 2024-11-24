package com.example.rivertech.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class TransactionHistory {
    @Id
    private long id;

}
