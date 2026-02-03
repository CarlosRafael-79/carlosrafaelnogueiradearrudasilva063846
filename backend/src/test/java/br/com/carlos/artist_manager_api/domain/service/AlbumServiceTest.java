package br.com.carlos.artist_manager_api.domain.service;

import br.com.carlos.artist_manager_api.api.dto.AlbumInput;
import br.com.carlos.artist_manager_api.api.dto.AlbumResponse;
import br.com.carlos.artist_manager_api.api.dto.ImagemAlbumResponse;
import br.com.carlos.artist_manager_api.api.mapper.AlbumMapper;
import br.com.carlos.artist_manager_api.domain.entity.Album;
import br.com.carlos.artist_manager_api.domain.entity.Artista;
import br.com.carlos.artist_manager_api.domain.entity.GeneroMusical;
import br.com.carlos.artist_manager_api.domain.entity.ImagemAlbum;
import br.com.carlos.artist_manager_api.domain.repository.AlbumRepository;
import br.com.carlos.artist_manager_api.domain.repository.ArtistaRepository;
import br.com.carlos.artist_manager_api.domain.repository.GeneroMusicalRepository;
import br.com.carlos.artist_manager_api.infrastructure.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @InjectMocks
    private AlbumService service;

    @Mock private AlbumRepository albumRepository;
    @Mock private ArtistaRepository artistaRepository;
    @Mock private GeneroMusicalRepository generoRepository;
    @Mock private FileStorageService storageService;
    @Mock private AlbumMapper albumMapper;



    @Test
    @DisplayName("Deve salvar álbum simples sem vínculos")
    void deveSalvarAlbumSimples() {
        AlbumInput input = new AlbumInput("Hybrid Theory", 2000, 3600, null, null);

        Album albumConvertido = new Album();
        albumConvertido.setTitulo("Hybrid Theory");

        Album albumSalvo = new Album();
        albumSalvo.setId(UUID.randomUUID());
        albumSalvo.setTitulo("Hybrid Theory");

        AlbumResponse responseEsperado = new AlbumResponse(
                albumSalvo.getId(), "Hybrid Theory", 2000, 3600, Set.of(), Set.of(), Set.of()
        );

        when(albumMapper.toEntity(input)).thenReturn(albumConvertido);
        when(albumRepository.save(any(Album.class))).thenReturn(albumSalvo);
        when(albumMapper.toResponse(any(Album.class), any())).thenReturn(responseEsperado);

        AlbumResponse response = service.salvar(input);

        assertNotNull(response.id());
        assertEquals("Hybrid Theory", response.titulo());
    }

    @Test
    @DisplayName("Deve salvar álbum com relacionamentos (Artistas e Gêneros)")
    void deveSalvarAlbumComRelacionamentos() {
        UUID artistaId = UUID.randomUUID();
        UUID generoId = UUID.randomUUID();
        AlbumInput input = new AlbumInput("Meteora", 2003, 3000, Set.of(artistaId), Set.of(generoId));

        Album albumConvertido = new Album();
        Album albumSalvo = new Album();

        when(albumMapper.toEntity(input)).thenReturn(albumConvertido);
        when(artistaRepository.findAllById(input.artistasIds())).thenReturn(List.of(new Artista()));
        when(generoRepository.findAllById(input.generosIds())).thenReturn(List.of(new GeneroMusical()));
        when(albumRepository.save(any(Album.class))).thenReturn(albumSalvo);
        when(albumMapper.toResponse(any(Album.class), any())).thenReturn(new AlbumResponse(UUID.randomUUID(), "Meteora", 2003, 3000, Set.of(), Set.of(), Set.of()));

        service.salvar(input);

        assertEquals(1, albumConvertido.getArtistas().size());
        assertEquals(1, albumConvertido.getGeneros().size());
        verify(albumRepository).save(albumConvertido);
    }


    @Test
    @DisplayName("Deve atualizar álbum com sucesso")
    void deveAtualizarAlbumComSucesso() {
        UUID id = UUID.randomUUID();
        AlbumInput input = new AlbumInput("Novo Titulo", 2022, 500, null, null);

        Album albumExistente = new Album();
        albumExistente.setId(id);

        when(albumRepository.findById(id)).thenReturn(Optional.of(albumExistente));
        when(albumRepository.save(albumExistente)).thenReturn(albumExistente);

        service.atualizar(id, input);

        verify(albumMapper).copyToEntity(input, albumExistente);
        verify(albumRepository).save(albumExistente);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar álbum inexistente")
    void deveLancarErroAoAtualizarAlbumInexistente() {
        UUID id = UUID.randomUUID();
        AlbumInput input = new AlbumInput("Titulo", 2000, 100, null, null);

        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.atualizar(id, input));
        verify(albumRepository, never()).save(any());
    }



    @Test
    @DisplayName("Deve fazer upload de imagem com sucesso")
    void deveFazerUploadImagens() {
        UUID albumId = UUID.randomUUID();
        Album albumMock = new Album();
        albumMock.setId(albumId);
        MockMultipartFile arquivo = new MockMultipartFile("imagens", "foto.jpg", "image/jpeg", "bytes".getBytes());

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(albumMock));

        service.uploadImagens(albumId, List.of(arquivo));

        verify(storageService, times(1)).upload(anyString(), any(InputStream.class), eq("image/jpeg"));
        verify(albumRepository, times(1)).save(albumMock);
    }

    @Test
    @DisplayName("Deve lançar erro se o upload falhar")
    void deveLancarErroSeUploadFalhar() {
        UUID albumId = UUID.randomUUID();
        MockMultipartFile arquivo = new MockMultipartFile("imagens", "teste.jpg", "image/jpeg", "bytes".getBytes());

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(new Album()));
        doThrow(new RuntimeException("Erro S3")).when(storageService).upload(anyString(), any(), anyString());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.uploadImagens(albumId, List.of(arquivo))
        );
        assertEquals("Falha ao processar upload", ex.getMessage());
    }


    @Test
    @DisplayName("Deve listar álbuns paginados com filtros de Título e Artista")
    void deveListarComFiltros() {
        String titulo = "Meteora";
        String nomeArtista = "Linkin Park";
        Pageable pageable = PageRequest.of(0, 10);

        Album album = new Album();
        Page<Album> paginaBanco = new PageImpl<>(List.of(album));
        AlbumResponse responseMock = new AlbumResponse(UUID.randomUUID(), "Meteora", 2003, 3000, Set.of(), Set.of(), Set.of());

        when(albumRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paginaBanco);
        when(albumMapper.toResponse(eq(album), any())).thenReturn(responseMock);

        Page<AlbumResponse> resultado = service.listar(titulo, nomeArtista, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());

        verify(albumRepository).findAll(any(Specification.class), eq(pageable));
        verify(albumMapper).toResponse(album, storageService);
    }

    @Test
    @DisplayName("Deve listar álbuns sem filtros (parâmetros nulos)")
    void deveListarSemFiltros() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Album> paginaBanco = new PageImpl<>(List.of(new Album()));

        when(albumRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paginaBanco);
        when(albumMapper.toResponse(any(), any())).thenReturn(null);

        service.listar(null, null, pageable);

        verify(albumRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve gerar URL assinada corretamente no mapeamento da lista")
    void deveGerarUrlAssinadaAoListar() {

        ImagemAlbum img = new ImagemAlbum();
        img.setUrl("uuid-arquivo.jpg");
        Album album = new Album();
        album.adicionarImagem(img);

        Page<Album> paginaBanco = new PageImpl<>(List.of(album));


        AlbumResponse responseMock = new AlbumResponse(
                UUID.randomUUID(), "Meteora", 2003, 3000, Set.of(), Set.of(),
                Set.of(new ImagemAlbumResponse(UUID.randomUUID(), "capa.jpg", "http://minio-assinado"))
        );

        when(albumRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(paginaBanco);
        when(albumMapper.toResponse(eq(album), eq(storageService))).thenReturn(responseMock);

        Page<AlbumResponse> resultado = service.listar(null, null, PageRequest.of(0, 10));

        String url = resultado.getContent().get(0).imagens().iterator().next().urlAssinada();
        assertEquals("http://minio-assinado", url);
    }
}