package com.example.rivertech;

import com.example.rivertech.dto.BetHistoryDto;
import com.example.rivertech.dto.GameResultDto;
import com.example.rivertech.model.Bet;
import com.example.rivertech.model.User;
import com.example.rivertech.model.Transaction;
import com.example.rivertech.model.User;
import com.example.rivertech.model.enums.BetStatus;
import com.example.rivertech.repository.BetRepository;
import com.example.rivertech.service.BetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetServiceTest {

    @Mock
    private BetRepository betRepository;

    @InjectMocks
    private BetService betService;

    @Captor
    private ArgumentCaptor<Bet> betCaptor;

    private Bet createBet(long id, User user, BigDecimal amount, int number) {
        return Bet.builder()
                .id(id)
                .user(user)
                .betAmount(amount)
                .betNumber(number)
                .status(BetStatus.PENDING)
                .build();
    }

    private User createUser(long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .build();
    }

    @Test
    void getBetsForUser_shouldReturnBets() {
        // given
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Bet> betList = List.of(
                createBet(1L, createUser(userId, "testUser"), BigDecimal.TEN, 5)
        );
        Page<Bet> expectedBets = new PageImpl<>(betList, pageable, betList.size());
        when(betRepository.findByUserId(userId, pageable)).thenReturn(expectedBets);

        // when
        Page<Bet> actualBets = betService.getBetsForUser(userId, pageable);

        // then
        assertThat(actualBets.getContent()).isEqualTo(expectedBets.getContent());
        assertThat(actualBets.getTotalElements()).isEqualTo(expectedBets.getTotalElements());
        verify(betRepository, times(1)).findByUserId(userId, pageable);
    }

    @Test
    void getBetsForUser_shouldReturnEmptyPage_whenNoBetsFound() {
        // given
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bet> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(betRepository.findByUserId(userId, pageable)).thenReturn(emptyPage);

        // when
        Page<Bet> actualBets = betService.getBetsForUser(userId, pageable);

        // then
        assertThat(actualBets.getContent()).isEmpty();
        assertThat(actualBets.getTotalElements()).isEqualTo(0);
        verify(betRepository, times(1)).findByUserId(userId, pageable);
    }

    @Test
    void createPendingBet_shouldCreateAndReturnBet() {
        // given
        User user = createUser(1L, "testUser");
        Transaction transaction = Transaction.builder().id(1L).build();
        BigDecimal betAmount = BigDecimal.TEN;
        int chosenNumber = 5;

        Bet savedBet = createBet(1L, user, betAmount, chosenNumber);
        when(betRepository.save(any(Bet.class))).thenReturn(savedBet);

        // when
        Bet result = betService.createPendingBet(user, betAmount, chosenNumber, transaction);

        // then
        assertThat(result).isEqualTo(savedBet);
        verify(betRepository, times(1)).save(any(Bet.class));
    }

    @Test
    void createPendingBet_shouldThrowException_whenBetAmountIsNegative() {
        // given
        User user = createUser(1L, "testUser");
        Transaction transaction = mock(Transaction.class);
        BigDecimal negativeAmount = BigDecimal.valueOf(-10);

        // when/then
        assertThatThrownBy(() -> betService.createPendingBet(user, negativeAmount, 5, transaction))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bet amount must be greater than zero.");
    }

    @Test
    void finalizeBet_shouldUpdateBetAndSave() {
        // given
        Bet bet = createBet(1L, createUser(1L, "testUser"), BigDecimal.TEN, 5);
        bet.setStatus(BetStatus.PENDING);
        GameResultDto gameResultDto = new GameResultDto(7, BigDecimal.valueOf(50));

        Bet savedBet = createBet(1L, createUser(1L, "testUser"), BigDecimal.TEN, 5);
        savedBet.setStatus(BetStatus.COMPLETED);
        savedBet.setGeneratedNumber(gameResultDto.getGeneratedNumber());
        savedBet.setWinnings(gameResultDto.getWinnings());

        when(betRepository.save(any(Bet.class))).thenReturn(savedBet);

        // when
        betService.finalizeBet(bet, gameResultDto);

        // then
        verify(betRepository).save(betCaptor.capture());
        Bet capturedBet = betCaptor.getValue();

        assertThat(capturedBet.getStatus()).isEqualTo(BetStatus.COMPLETED);
        assertThat(capturedBet.getGeneratedNumber()).isEqualTo(gameResultDto.getGeneratedNumber());
        assertThat(capturedBet.getWinnings()).isEqualTo(gameResultDto.getWinnings());
    }


    @Test
    void getBetHistoryForUser_shouldReturnPaginatedBetHistory() {
        // given
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Bet bet = createBet(1L, createUser(userId, "testUser"), BigDecimal.TEN, 5);
        bet.setGeneratedNumber(7);
        bet.setWinnings(BigDecimal.valueOf(50));
        bet.setStatus(BetStatus.COMPLETED);

        Page<Bet> betsPage = new PageImpl<>(List.of(bet));
        when(betRepository.findByUserId(userId, pageable)).thenReturn(betsPage);

        // when
        Page<BetHistoryDto> result = betService.getBetHistoryForUser(userId, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        BetHistoryDto betHistoryDto = result.getContent().get(0);
        assertThat(betHistoryDto.getBetAmount()).isEqualTo(bet.getBetAmount());
        assertThat(betHistoryDto.getBetNumber()).isEqualTo(bet.getBetNumber());
        assertThat(betHistoryDto.getGeneratedNumber()).isEqualTo(bet.getGeneratedNumber());
        assertThat(betHistoryDto.getWinnings()).isEqualTo(bet.getWinnings());
        assertThat(betHistoryDto.getStatus()).isEqualTo(bet.getStatus());
        verify(betRepository, times(1)).findByUserId(userId, pageable);
    }

    @Test
    void getBetHistoryForUser_shouldReturnEmptyPage_whenNoBetsFound() {
        // given
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        when(betRepository.findByUserId(userId, pageable)).thenReturn(Page.empty());

        // when
        Page<BetHistoryDto> result = betService.getBetHistoryForUser(userId, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        verify(betRepository, times(1)).findByUserId(userId, pageable);
    }
}

