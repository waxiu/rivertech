package com.example.rivertech.dto;

import com.example.rivertech.model.enums.BetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BetDto {
    private Long id;
    private BigDecimal betAmount;
    private int betNumber;
    private BetStatus status;
    private Long userId;

}
