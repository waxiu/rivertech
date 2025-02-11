package com.example.sportbet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequestDto {
    private String name;
    private String surname;
    private String email;
    private String password;
    private String username;
}
