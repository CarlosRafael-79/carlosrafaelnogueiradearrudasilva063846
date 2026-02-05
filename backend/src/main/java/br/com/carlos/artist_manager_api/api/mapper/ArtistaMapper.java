package br.com.carlos.artist_manager_api.api.mapper;


import br.com.carlos.artist_manager_api.api.dto.ArtistaInput;
import br.com.carlos.artist_manager_api.api.dto.ArtistaResponse;
import br.com.carlos.artist_manager_api.domain.entity.Artista;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ArtistaMapper {

    ArtistaResponse toResponse(Artista artista);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    Artista toEntity(ArtistaInput input);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    void copyToEntity(ArtistaInput input, @MappingTarget Artista artista);
}