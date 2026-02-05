package br.com.carlos.artist_manager_api.api.dto;

import java.util.UUID;

public record ArtistaResponse(
        UUID id,
        String nome,
        String paisOrigem,
        Integer anoFormacao,
        String descricao
) {}