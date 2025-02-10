package com.example.rivertech.service;

import com.example.rivertech.model.User;
import com.example.rivertech.repository.UserRepository;
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
    private final UserRepository userRepository;

    public LeaderboardService(RedisTemplate<String, Long> redisTemplate, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    public Set<ZSetOperations.TypedTuple<Long>> getTopUsers(int top) {
        logger.info("Fetching top {} users from leaderboard", top);
        Set<ZSetOperations.TypedTuple<Long>> topUsers = redisTemplate.opsForZSet()
                .reverseRangeWithScores(REDIS_LEADERBOARD_KEY, 0, Math.max(0, top - 1));
        if (topUsers.isEmpty()) {
            logger.warn("No users found in the leaderboard");
        } else {
            logger.info("Fetched {} users from leaderboard", topUsers.size());
        }
        return topUsers;
    }

    @Scheduled(fixedRate = 10000)
    public void updateLeaderboardInBatch() {
        logger.info("Starting leaderboard update");

        List<User> users = userRepository.findAll();
        logger.debug("Fetched {} users from the database", users.size());

        redisTemplate.delete(REDIS_LEADERBOARD_KEY);
        logger.debug("Cleared old leaderboard from Redis");

        for (User user : users) {
            BigDecimal totalWinnings = user.getWallet().getTotalWinnings();
            redisTemplate.opsForZSet().add(REDIS_LEADERBOARD_KEY, user.getId(), totalWinnings.doubleValue());
            logger.debug("Updated userId: {} with totalWinnings: {} in leaderboard", user.getId(), totalWinnings);
        }

        logger.info("Leaderboard update completed");
    }
}
