package br.com.carlos.artist_manager_api.api.dto;

import java.util.UUID;

public record GeneroResponse(
        UUID id,
        String nome,
        String slug
) {}