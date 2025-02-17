package com.example.sportbet;

import com.example.sportbet.model.User;
import com.example.sportbet.model.Wallet;
import com.example.sportbet.repository.UserRepository;
import com.example.sportbet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import com.example.sportbet.service.LeaderboardService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    private static final String REDIS_LEADERBOARD_KEY = "leaderboard";

    @Mock
    private RedisTemplate<String, Long> redisTemplate;

    @Mock
    private ZSetOperations<String, Long> zSetOperations;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void shouldFetchTopUsersFromLeaderboard() {
        // given
        int top = 3;
        Set<ZSetOperations.TypedTuple<Long>> mockLeaderboard = Set.of(
                createMockTypedTuple(1L, 100.0),
                createMockTypedTuple(2L, 90.0),
                createMockTypedTuple(3L, 80.0)
        );

        when(zSetOperations.reverseRangeWithScores(REDIS_LEADERBOARD_KEY, 0, top - 1))
                .thenReturn(mockLeaderboard);

        // when
        Set<ZSetOperations.TypedTuple<Long>> topUsers = leaderboardService.getTopUsers(top);

        // then
        assertThat(topUsers).hasSize(3);
        assertThat(topUsers).extracting(ZSetOperations.TypedTuple::getValue).containsExactlyInAnyOrder(1L, 2L, 3L);
        verify(zSetOperations).reverseRangeWithScores(REDIS_LEADERBOARD_KEY, 0, top - 1);
    }

    @Test
    void shouldWarnWhenLeaderboardIsEmpty() {
        // given
        int top = 3;
        when(zSetOperations.reverseRangeWithScores(REDIS_LEADERBOARD_KEY, 0, top - 1)).thenReturn(Set.of());

        // when
        Set<ZSetOperations.TypedTuple<Long>> topUsers = leaderboardService.getTopUsers(top);

        // then
        assertThat(topUsers).isEmpty();
        verify(zSetOperations).reverseRangeWithScores(REDIS_LEADERBOARD_KEY, 0, top - 1);
    }

    @Test
    void shouldUpdateLeaderboardInBatch() {
        // given
        User user1 = createMockUser(1L, BigDecimal.valueOf(100.0));
        User user2 = createMockUser(2L, BigDecimal.valueOf(200.0));

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(redisTemplate.delete(REDIS_LEADERBOARD_KEY)).thenReturn(true); // Zamiast doNothing()

        // when
        leaderboardService.updateLeaderboardInBatch();

        // then
        verify(userRepository).findAll();
        verify(redisTemplate).delete(REDIS_LEADERBOARD_KEY);
        verify(zSetOperations).add(REDIS_LEADERBOARD_KEY, 1L, 100.0);
        verify(zSetOperations).add(REDIS_LEADERBOARD_KEY, 2L, 200.0);
    }

    // Helper methods for test data creation
    private ZSetOperations.TypedTuple<Long> createMockTypedTuple(Long value, Double score) {
        ZSetOperations.TypedTuple<Long> typedTuple = mock(ZSetOperations.TypedTuple.class);
        lenient().when(typedTuple.getValue()).thenReturn(value); // Użycie lenient()
        lenient().when(typedTuple.getScore()).thenReturn(score); // Użycie lenient()
        return typedTuple;
    }


    private User createMockUser(Long id, BigDecimal totalWinnings) {
        Wallet wallet = mock(Wallet.class);
        when(wallet.getTotalWinnings()).thenReturn(totalWinnings);

        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getWallet()).thenReturn(wallet);

        return user;
    }
}