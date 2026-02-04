package br.com.carlos.artist_manager_api.api.controller;

import br.com.carlos.artist_manager_api.api.dto.AlbumInput;
import br.com.carlos.artist_manager_api.domain.entity.Album;
import br.com.carlos.artist_manager_api.domain.entity.Artista;
import br.com.carlos.artist_manager_api.domain.entity.GeneroMusical;
import br.com.carlos.artist_manager_api.domain.repository.AlbumRepository;
import br.com.carlos.artist_manager_api.domain.repository.ArtistaRepository;
import br.com.carlos.artist_manager_api.domain.repository.GeneroMusicalRepository;
import br.com.carlos.artist_manager_api.infrastructure.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlbumControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AlbumRepository albumRepository;
    @Autowired private ArtistaRepository artistaRepository;
    @Autowired private GeneroMusicalRepository generoRepository;

    @MockitoBean
    private FileStorageService fileStorageService;

    private Artista artistaPadrao;
    private GeneroMusical generoPadrao;

    @BeforeEach
    void setup() {
        albumRepository.deleteAll();
        artistaRepository.deleteAll();
        generoRepository.deleteAll();

        artistaPadrao = new Artista();
        artistaPadrao.setNome("Linkin Park");
        artistaRepository.save(artistaPadrao);

        generoPadrao = new GeneroMusical();
        generoPadrao.setNome("Nu Metal");
        generoPadrao.setSlug("nu-metal");
        generoRepository.save(generoPadrao);
    }


    @Test
    @DisplayName("Deve criar álbum com artistas e gêneros vinculados")
    void deveCriarAlbumCompleto() throws Exception {
        AlbumInput input = new AlbumInput(
                "Hybrid Theory",
                2000,
                3600,
                Set.of(artistaPadrao.getId()),
                Set.of(generoPadrao.getId())
        );

        mockMvc.perform(post("/v1/albuns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Hybrid Theory"))
                .andExpect(jsonPath("$.anoLancamento").value(2000))
                .andExpect(jsonPath("$.artistas[0].nome").value("Linkin Park"))
                .andExpect(jsonPath("$.generos[0].slug").value("nu-metal"));
    }

    @Test
    @DisplayName("Deve falhar ao criar álbum sem título (Validação)")
    void deveFalharSemTitulo() throws Exception {
        AlbumInput input = new AlbumInput(
                "",
                2022,
                300,
                Set.of(),
                Set.of()
        );

        mockMvc.perform(post("/v1/albuns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de Validação"));
    }



    @Test
    @DisplayName("Deve atualizar álbum existente com sucesso")
    void deveAtualizarAlbum() throws Exception {
        Album album = new Album();
        album.setTitulo("Meteora");
        album.setAnoLancamento(2003);
        album.setDuracaoSegundos(3000);
        albumRepository.save(album);

        AlbumInput inputAtualizacao = new AlbumInput(
                "Meteora (20th Anniversary)",
                2023,
                3200,
                Set.of(artistaPadrao.getId()),
                null
        );

        mockMvc.perform(put("/v1/albuns/{id}", album.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Meteora (20th Anniversary)"))
                .andExpect(jsonPath("$.anoLancamento").value(2023))
                .andExpect(jsonPath("$.artistas[0].nome").value("Linkin Park"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar álbum inexistente")
    void deveRetornar404AoAtualizarInexistente() throws Exception {
        AlbumInput input = new AlbumInput("Teste", 2000, 100, null, null);

        mockMvc.perform(put("/v1/albuns/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Deve listar álbuns com paginação e filtro por nome do artista")
    void deveListarComFiltros() throws Exception {

        criarAlbum("Hybrid Theory", 2000, artistaPadrao);
        criarAlbum("Meteora", 2003, artistaPadrao);

        Artista coldplay = new Artista();
        coldplay.setNome("Coldplay");
        artistaRepository.save(coldplay);
        criarAlbum("Parachutes", 2000, coldplay);

        mockMvc.perform(get("/v1/albuns")
                        .param("nomeArtista", "Linkin")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2))) // Deve achar 2
                .andExpect(jsonPath("$.content[0].artistas[0].nome", is("Linkin Park")))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    @Test
    @DisplayName("Deve listar álbuns filtrando por título parcial")
    void deveFiltrarPorTitulo() throws Exception {
        criarAlbum("Hybrid Theory", 2000, artistaPadrao);
        criarAlbum("Meteora", 2003, artistaPadrao);

        mockMvc.perform(get("/v1/albuns")
                        .param("titulo", "Hybrid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].titulo", is("Hybrid Theory")));
    }


    @Test
    @DisplayName("Deve realizar upload de imagem e vincular ao álbum")
    void deveFazerUploadImagem() throws Exception {

        Album album = new Album();
        album.setTitulo("Capa Teste");
        album = albumRepository.save(album);

        // 2. Mock do Arquivo
        MockMultipartFile arquivo = new MockMultipartFile(
                "imagens",
                "capa.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "conteudo-da-imagem".getBytes()
        );


        doNothing().when(fileStorageService).upload(anyString(), any(InputStream.class), anyString());

        mockMvc.perform(multipart(HttpMethod.POST, "/v1/albuns/{id}/imagens", album.getId())
                        .file(arquivo)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        verify(fileStorageService).upload(anyString(), any(InputStream.class), eq("image/jpeg"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar upload para álbum inexistente")
    void deveFalharUploadAlbumInexistente() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "imagens", "teste.jpg", "image/jpeg", "bytes".getBytes()
        );

        mockMvc.perform(multipart("/v1/albuns/{id}/imagens", UUID.randomUUID())
                        .file(arquivo))
                .andExpect(status().isNotFound());
    }

    // Método auxiliar para criar álbuns no teste
    private void criarAlbum(String titulo, int ano, Artista artista) {
        Album album = new Album();
        album.setTitulo(titulo);
        album.setAnoLancamento(ano);
        if (artista != null) {
            album.getArtistas().add(artista);
        }
        albumRepository.save(album);
    }
}