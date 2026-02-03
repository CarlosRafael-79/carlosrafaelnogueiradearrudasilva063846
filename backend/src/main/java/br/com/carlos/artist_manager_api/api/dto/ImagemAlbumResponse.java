package br.com.carlos.artist_manager_api.api.dto;

import java.util.UUID;

public record ImagemAlbumResponse(
        UUID id,
        String nomeArquivo,
        String urlAssinada
) {}