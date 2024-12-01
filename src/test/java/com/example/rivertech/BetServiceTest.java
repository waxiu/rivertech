package com.example.rivertech;

import com.example.rivertech.dto.BetHistoryDto;
import com.example.rivertech.dto.GameResultDto;
import com.example.rivertech.model.Bet;
import com.example.rivertech.model.Player;
import com.example.rivertech.model.Transaction;
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

    private Bet createBet(long id, Player player, BigDecimal amount, int number) {
        return Bet.builder()
                .id(id)
                .player(player)
                .betAmount(amount)
                .betNumber(number)
                .status(BetStatus.PENDING)
                .build();
    }

    private Player createPlayer(long id, String username) {
        return Player.builder()
                .id(id)
                .username(username)
                .build();
    }

//    @Test
//    void getBetsForPlayer_shouldReturnBets() {
//        // given
//        long playerId = 1L;
//        List<Bet> expectedBets = List.of(
//                createBet(1L, createPlayer(playerId, "testPlayer"), BigDecimal.TEN, 5)
//        );
//        when(betRepository.findAllByPlayerId(playerId)).thenReturn(expectedBets);
//
//        // when
//        List<Bet> actualBets = betService.getBetsForPlayer(playerId);
//
//        // then
//        assertThat(actualBets).isEqualTo(expectedBets);
//        verify(betRepository, times(1)).findAllByPlayerId(playerId);
//    }
//
//    @Test
//    void getBetsForPlayer_shouldReturnEmptyList_whenNoBetsFound() {
//        // given
//        long playerId = 1L;
//        when(betRepository.findAllByPlayerId(playerId)).thenReturn(Collections.emptyList());
//
//        // when
//        List<Bet> actualBets = betService.getBetsForPlayer(playerId);
//
//        // then
//        assertThat(actualBets).isEmpty();
//        verify(betRepository, times(1)).findAllByPlayerId(playerId);
//    }

    @Test
    void createPendingBet_shouldCreateAndReturnBet() {
        // given
        Player player = createPlayer(1L, "testPlayer");
        Transaction transaction = Transaction.builder().id(1L).build();
        BigDecimal betAmount = BigDecimal.TEN;
        int chosenNumber = 5;

        Bet savedBet = createBet(1L, player, betAmount, chosenNumber);
        when(betRepository.save(any(Bet.class))).thenReturn(savedBet);

        // when
        Bet result = betService.createPendingBet(player, betAmount, chosenNumber, transaction);

        // then
        assertThat(result).isEqualTo(savedBet);
        verify(betRepository, times(1)).save(any(Bet.class));
    }

    @Test
    void createPendingBet_shouldThrowException_whenBetAmountIsNegative() {
        // given
        Player player = createPlayer(1L, "testPlayer");
        Transaction transaction = mock(Transaction.class);
        BigDecimal negativeAmount = BigDecimal.valueOf(-10);

        // when/then
        assertThatThrownBy(() -> betService.createPendingBet(player, negativeAmount, 5, transaction))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bet amount must be greater than zero.");
    }

    @Test
    void finalizeBet_shouldUpdateBetAndSave() {
        // given
        Bet bet = createBet(1L, createPlayer(1L, "testPlayer"), BigDecimal.TEN, 5);
        GameResultDto gameResultDto = new GameResultDto(7, BigDecimal.valueOf(50));

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
    void getBetHistoryForPlayer_shouldReturnPaginatedBetHistory() {
        // given
        long playerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Bet bet = createBet(1L, createPlayer(playerId, "testPlayer"), BigDecimal.TEN, 5);
        bet.setGeneratedNumber(7);
        bet.setWinnings(BigDecimal.valueOf(50));
        bet.setStatus(BetStatus.COMPLETED);

        Page<Bet> betsPage = new PageImpl<>(List.of(bet));
        when(betRepository.findByPlayerId(playerId, pageable)).thenReturn(betsPage);

        // when
        Page<BetHistoryDto> result = betService.getBetHistoryForPlayer(playerId, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        BetHistoryDto betHistoryDto = result.getContent().get(0);
        assertThat(betHistoryDto.getBetAmount()).isEqualTo(bet.getBetAmount());
        assertThat(betHistoryDto.getBetNumber()).isEqualTo(bet.getBetNumber());
        assertThat(betHistoryDto.getGeneratedNumber()).isEqualTo(bet.getGeneratedNumber());
        assertThat(betHistoryDto.getWinnings()).isEqualTo(bet.getWinnings());
        assertThat(betHistoryDto.getStatus()).isEqualTo(bet.getStatus());
        verify(betRepository, times(1)).findByPlayerId(playerId, pageable);
    }

    @Test
    void getBetHistoryForPlayer_shouldReturnEmptyPage_whenNoBetsFound() {
        // given
        long playerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        when(betRepository.findByPlayerId(playerId, pageable)).thenReturn(Page.empty());

        // when
        Page<BetHistoryDto> result = betService.getBetHistoryForPlayer(playerId, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        verify(betRepository, times(1)).findByPlayerId(playerId, pageable);
    }
}

