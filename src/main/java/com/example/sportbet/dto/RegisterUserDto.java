package com.example.sportbet.dto;

import com.example.sportbet.model.enums.BetStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
public class RegisterUserDto {
    private String name;
    private String surname;
    private String email;
    private String password;
    private String username;
}
