package com.example.sportbet;

import com.example.sportbet.dto.response.UserRegistrationRequestDto;
import com.example.sportbet.model.User;
import com.example.sportbet.model.Wallet;
import com.example.sportbet.repository.UserRepository;
import com.example.sportbet.service.AuthService;
import com.example.sportbet.service.UserService;
import com.example.sportbet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private AuthService authService;
    @Test
    void shouldRegisterUserSuccessfully() {
        // given
        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setUsername("john.doe");
        dto.setPassword("password");
        dto.setEmail("john.doe@example.com");

        String encodedPassword = "encodedPassword123"; // Symulujemy zakodowane hasło

        when(passwordEncoder.encode(dto.getPassword())).thenReturn(encodedPassword);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L); // Symulacja ustawienia ID w repozytorium
            return user;
        });

        // when
        User savedUser = authService.registerUser(dto);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(1L);
        assertThat(savedUser.getName()).isEqualTo(dto.getName());
        assertThat(savedUser.getSurname()).isEqualTo(dto.getSurname());
        assertThat(savedUser.getUsername()).isEqualTo(dto.getUsername());
        assertThat(savedUser.getEmail()).isEqualTo(dto.getEmail());
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword); // Sprawdzamy zakodowane hasło

        verify(passwordEncoder).encode(dto.getPassword()); // Upewniamy się, że encoder został wywołany
        verify(userRepository).save(any(User.class));
        verify(walletService).createWalletForUser(savedUser);
    }
    @Test
    void shouldDepositToUserWalletSuccessfully() {
        // given
        Long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(100);
        Wallet wallet = new Wallet();
        User user = User.builder().wallet(wallet).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        userService.depositToUserWallet(userId, amount);

        // then
        verify(userRepository).findById(userId);
        verify(walletService).depositFunds(wallet, amount);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForDeposit() {
        // given
        Long userId = 99L;
        BigDecimal amount = BigDecimal.valueOf(50);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userService.depositToUserWallet(userId, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found with ID: " + userId);

        verify(userRepository).findById(userId);
        verifyNoInteractions(walletService);
    }
}

