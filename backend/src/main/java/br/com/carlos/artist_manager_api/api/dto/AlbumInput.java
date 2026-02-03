package br.com.carlos.artist_manager_api.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public record AlbumInput(
        @NotBlank(message = "O título é obrigatório")
        String titulo,

        @NotNull(message = "O ano de lançamento é obrigatório")
        Integer anoLancamento,

        Integer duracaoSegundos,

        Set<UUID> artistasIds,
        Set<UUID> generosIds
) {}