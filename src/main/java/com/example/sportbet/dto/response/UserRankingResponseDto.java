package com.example.sportbet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRankingResponseDto {
    private Long userId;
    private double score;
}
