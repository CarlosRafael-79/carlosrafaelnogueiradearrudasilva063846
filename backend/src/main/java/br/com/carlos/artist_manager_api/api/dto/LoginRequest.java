package br.com.carlos.artist_manager_api.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String username,

        @NotBlank(message = "A senha é obrigatória")
        String password
) {}