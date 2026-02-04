package br.com.carlos.artist_manager_api.api.controller;

import br.com.carlos.artist_manager_api.api.dto.ArtistaInput;
import br.com.carlos.artist_manager_api.domain.entity.Artista;
import br.com.carlos.artist_manager_api.domain.repository.ArtistaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArtistaControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ArtistaRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }


    @Test
    @DisplayName("Deve criar um artista com sucesso (201 Created)")
    void deveCriarArtistaComSucesso() throws Exception {
        ArtistaInput input = new ArtistaInput(
                "Guns N' Roses",
                "USA",
                1985,
                "Hard Rock band"
        );

        mockMvc.perform(post("/v1/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Guns N' Roses"));
    }

    @Test
    @DisplayName("Deve falhar ao criar artista sem nome (400 Bad Request)")
    void deveFalharSemNome() throws Exception {
        ArtistaInput input = new ArtistaInput(
                "",
                "USA",
                1985,
                "Band"
        );

        mockMvc.perform(post("/v1/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de Validação"));
    }


    @Test
    @DisplayName("Deve atualizar artista existente com sucesso")
    void deveAtualizarArtista() throws Exception {
        Artista artista = new Artista();
        artista.setNome("Angra");
        artista.setPaisOrigem("BRA");
        repository.save(artista);

        ArtistaInput input = new ArtistaInput("Angra (Updated)", "BRA", 1991, "Power Metal");

        mockMvc.perform(put("/v1/artistas/{id}", artista.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Angra (Updated)"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar artista inexistente")
    void deveRetornar404AoAtualizarInexistente() throws Exception {
        ArtistaInput input = new ArtistaInput("Teste", "USA", 2000, "Desc");

        mockMvc.perform(put("/v1/artistas/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Deve listar artistas paginados")
    void deveListarArtistas() throws Exception {
        Artista a1 = new Artista(); a1.setNome("Aerosmith"); repository.save(a1);
        Artista a2 = new Artista(); a2.setNome("Bon Jovi"); repository.save(a2);

        mockMvc.perform(get("/v1/artistas")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    @DisplayName("Deve filtrar artistas por nome")
    void deveFiltrarPorNome() throws Exception {
        Artista a1 = new Artista(); a1.setNome("Iron Maiden"); repository.save(a1);
        Artista a2 = new Artista(); a2.setNome("Metallica"); repository.save(a2);

        mockMvc.perform(get("/v1/artistas")
                        .param("nome", "Iron")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", is("Iron Maiden")));
    }
}