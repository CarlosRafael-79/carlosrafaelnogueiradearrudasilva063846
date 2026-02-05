package br.com.carlos.artist_manager_api.api.mapper;

import br.com.carlos.artist_manager_api.api.dto.AlbumInput;
import br.com.carlos.artist_manager_api.api.dto.AlbumResponse;
import br.com.carlos.artist_manager_api.api.dto.ImagemAlbumResponse;
import br.com.carlos.artist_manager_api.domain.entity.Album;
import br.com.carlos.artist_manager_api.domain.entity.ImagemAlbum;
import br.com.carlos.artist_manager_api.infrastructure.service.FileStorageService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ArtistaMapper.class, GeneroMapper.class})
public interface AlbumMapper {


    @Mapping(target = "imagens", source = "imagens", qualifiedByName = "mapImagem")
    AlbumResponse toResponse(Album album, @Context FileStorageService storageService);


    @Named("mapImagem")
    default ImagemAlbumResponse mapImagem(ImagemAlbum imagem, @Context FileStorageService storageService) {
        if (imagem == null) return null;

        return new ImagemAlbumResponse(
                imagem.getId(),
                imagem.getNomeArquivo(),
                storageService.gerarUrlAssinada(imagem.getUrl())
        );
    }


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "artistas", ignore = true)
    @Mapping(target = "generos", ignore = true)
    @Mapping(target = "imagens", ignore = true)
    Album toEntity(AlbumInput input);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "artistas", ignore = true)
    @Mapping(target = "generos", ignore = true)
    @Mapping(target = "imagens", ignore = true)
    void copyToEntity(AlbumInput input, @MappingTarget Album album);
}