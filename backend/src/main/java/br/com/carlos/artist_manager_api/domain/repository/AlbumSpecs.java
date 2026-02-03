package br.com.carlos.artist_manager_api.domain.repository;

import br.com.carlos.artist_manager_api.domain.entity.Album;
import br.com.carlos.artist_manager_api.domain.entity.Artista;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class AlbumSpecs {

    public static Specification<Album> comFiltros(String titulo, String nomeArtista) {
        return (root, query, builder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            if (StringUtils.hasText(titulo)) {
                predicates.add(builder.like(builder.lower(root.get("titulo")), "%" + titulo.toLowerCase() + "%"));
            }

            if (StringUtils.hasText(nomeArtista)) {
                Join<Album, Artista> artistasJoin = root.join("artistas");
                predicates.add(builder.like(builder.lower(artistasJoin.get("nome")), "%" + nomeArtista.toLowerCase() + "%"));
            }

            return builder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}