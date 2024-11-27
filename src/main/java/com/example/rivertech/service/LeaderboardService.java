package com.example.rivertech.service;

import com.example.rivertech.model.Player;
import com.example.rivertech.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class LeaderboardService {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardService.class);

    private static final String REDIS_LEADERBOARD_KEY = "leaderboard";
    private final RedisTemplate<String, Long> redisTemplate;
    private final PlayerRepository playerRepository;

    public LeaderboardService(RedisTemplate<String, Long> redisTemplate, PlayerRepository playerRepository) {
        this.redisTemplate = redisTemplate;
        this.playerRepository = playerRepository;
    }

    public Set<ZSetOperations.TypedTuple<Long>> getTopPlayers(int top) {
        logger.info("Fetching top {} players from leaderboard", top);
        Set<ZSetOperations.TypedTuple<Long>> topPlayers = redisTemplate.opsForZSet()
                .reverseRangeWithScores(REDIS_LEADERBOARD_KEY, 0, Math.max(0, top - 1));
        if (topPlayers.isEmpty()) {
            logger.warn("No players found in the leaderboard");
        } else {
            logger.info("Fetched {} players from leaderboard", topPlayers.size());
        }
        return topPlayers;
    }

    @Scheduled(fixedRate = 10000)
    public void updateLeaderboardInBatch() {
        logger.info("Starting leaderboard update");

        List<Player> players = playerRepository.findAll();
        logger.debug("Fetched {} players from the database", players.size());

        redisTemplate.delete(REDIS_LEADERBOARD_KEY);
        logger.debug("Cleared old leaderboard from Redis");

        for (Player player : players) {
            BigDecimal totalWinnings = player.getWallet().getTotalWinnings();
            redisTemplate.opsForZSet().add(REDIS_LEADERBOARD_KEY, player.getId(), totalWinnings.doubleValue());
            logger.debug("Updated playerId: {} with totalWinnings: {} in leaderboard", player.getId(), totalWinnings);
        }

        logger.info("Leaderboard update completed");
    }
}
