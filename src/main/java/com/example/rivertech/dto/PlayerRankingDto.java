package com.example.rivertech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerRankingDto {
    private Long playerId;
    private double score;
}
