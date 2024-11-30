package com.example.rivertech;

import com.example.rivertech.dto.PlayerRegistrationDto;
import com.example.rivertech.model.Player;
import com.example.rivertech.model.Wallet;
import com.example.rivertech.repository.PlayerRepository;
import com.example.rivertech.service.PlayerService;
import com.example.rivertech.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private PlayerService playerService;
    @Test
    void shouldRegisterPlayerSuccessfully() {
        // given
        PlayerRegistrationDto dto = new PlayerRegistrationDto();
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setUsername("john.doe");

        Player mockPlayer = Player.builder()
                .name(dto.getName())
                .surname(dto.getSurname())
                .username(dto.getUsername())
                .build();
        mockPlayer.setId(1L); // Ustawienie ID gracza po zapisaniu w repozytorium

        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> {
            Player player = invocation.getArgument(0);
            player.setId(1L); // Symulowanie ustawienia ID w repozytorium
            return player;
        });

        // when
        Player savedPlayer = playerService.registerPlayer(dto);

        // then
        assertThat(savedPlayer).isNotNull();
        assertThat(savedPlayer.getId()).isEqualTo(1L);
        assertThat(savedPlayer.getName()).isEqualTo(dto.getName());
        assertThat(savedPlayer.getSurname()).isEqualTo(dto.getSurname());
        assertThat(savedPlayer.getUsername()).isEqualTo(dto.getUsername());
        verify(playerRepository).save(any(Player.class));
        verify(walletService).createWalletForPlayer(savedPlayer);
    }

    @Test
    void shouldDepositToPlayerWalletSuccessfully() {
        // given
        Long playerId = 1L;
        BigDecimal amount = BigDecimal.valueOf(100);
        Wallet wallet = new Wallet();
        Player player = Player.builder().wallet(wallet).build();

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        // when
        playerService.depositToPlayerWallet(playerId, amount);

        // then
        verify(playerRepository).findById(playerId);
        verify(walletService).depositFunds(wallet, amount);
    }

    @Test
    void shouldThrowExceptionWhenPlayerNotFoundForDeposit() {
        // given
        Long playerId = 99L;
        BigDecimal amount = BigDecimal.valueOf(50);

        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> playerService.depositToPlayerWallet(playerId, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player not found with ID: " + playerId);

        verify(playerRepository).findById(playerId);
        verifyNoInteractions(walletService);
    }
}

