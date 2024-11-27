package com.example.rivertech.service;

import com.example.rivertech.model.Player;
import com.example.rivertech.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class LeaderboardService {

    private static final String REDIS_LEADERBOARD_KEY = "leaderboard";
    private final RedisTemplate<String, Long> redisTemplate;
    private final PlayerRepository playerRepository;

    public LeaderboardService(RedisTemplate<String, Long> redisTemplate, PlayerRepository playerRepository) {
        this.redisTemplate = redisTemplate;
        this.playerRepository = playerRepository;
    }
    public Set<ZSetOperations.TypedTuple<Long>> getTopPlayers(int top) {
        return redisTemplate.opsForZSet()
                .reverseRangeWithScores(REDIS_LEADERBOARD_KEY, 0, Math.max(0, top - 1));
    }

    @Scheduled(fixedRate = 10000) // Co 10 sekund
    public void updateLeaderboardInBatch() {
        List<Player> players = playerRepository.findAll();

        // Usunięcie starego rankingu w Redis
        redisTemplate.delete(REDIS_LEADERBOARD_KEY);

        // Dodanie nowych danych do Redis
        for (Player player : players) {
            BigDecimal totalWinnings = player.getWallet().getTotalWinnings();
            redisTemplate.opsForZSet().add(REDIS_LEADERBOARD_KEY, player.getId(), totalWinnings.doubleValue());
        }
    }
}

