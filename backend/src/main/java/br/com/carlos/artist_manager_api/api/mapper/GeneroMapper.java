package br.com.carlos.artist_manager_api.api.mapper;

import br.com.carlos.artist_manager_api.api.dto.GeneroInput;
import br.com.carlos.artist_manager_api.api.dto.GeneroResponse;
import br.com.carlos.artist_manager_api.domain.entity.GeneroMusical;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GeneroMapper {

    GeneroResponse toResponse(GeneroMusical genero);

    @Mapping(target = "id", ignore = true)
    GeneroMusical toEntity(GeneroInput input);

    @Mapping(target = "id", ignore = true)
    void copyToEntity(GeneroInput input, @MappingTarget GeneroMusical genero);
}