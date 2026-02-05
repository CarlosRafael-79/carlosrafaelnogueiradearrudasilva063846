package br.com.carlos.artist_manager_api.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArtistaInput(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @Size(min = 3, max = 3, message = "O país deve ter 3 caracteres (ISO Alpha-3)")
        String paisOrigem,

        Integer anoFormacao,
        String descricao
) {}