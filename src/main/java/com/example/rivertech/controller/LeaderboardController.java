package com.example.rivertech.controller;

import com.example.rivertech.dto.PlayerRankingDto;
import com.example.rivertech.service.LeaderboardService;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/winners")
    public List<PlayerRankingDto> getLeaderboard(@RequestParam(defaultValue = "10") int top) {
        Set<ZSetOperations.TypedTuple<Long>> leaderboard = leaderboardService.getTopPlayers(top);

        return leaderboard.stream()
                .map(entry -> new PlayerRankingDto(
                        entry.getValue(),  // playerId
                        entry.getScore()         // score
                ))
                .collect(Collectors.toList());
    }
}

