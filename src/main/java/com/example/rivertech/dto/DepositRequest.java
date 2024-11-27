package com.example.rivertech.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DepositRequest {
    private BigDecimal amount;
}

