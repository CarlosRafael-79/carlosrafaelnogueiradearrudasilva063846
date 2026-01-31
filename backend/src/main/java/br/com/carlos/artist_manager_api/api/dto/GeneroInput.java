package br.com.carlos.artist_manager_api.api.dto;

import jakarta.validation.constraints.NotBlank;

public record GeneroInput(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O slug é obrigatório")
        String slug
) {}