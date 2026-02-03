package br.com.carlos.artist_manager_api.api.dto;

import java.util.Set;
import java.util.UUID;

public record AlbumResponse(
        UUID id,
        String titulo,
        Integer anoLancamento,
        Integer duracaoSegundos,
        Set<ArtistaResponse> artistas,
        Set<GeneroResponse> generos,
        Set<ImagemAlbumResponse> imagens
) {}