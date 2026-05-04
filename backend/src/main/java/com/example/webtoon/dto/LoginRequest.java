package com.example.webtoon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Size(max = 255)
    private String username;

    @NotBlank
    @Size(max = 128)
    private String password;
}
