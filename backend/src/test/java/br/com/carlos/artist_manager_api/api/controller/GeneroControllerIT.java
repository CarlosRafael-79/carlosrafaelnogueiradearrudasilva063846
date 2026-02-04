package br.com.carlos.artist_manager_api.api.controller;

import br.com.carlos.artist_manager_api.api.dto.GeneroInput;
import br.com.carlos.artist_manager_api.domain.entity.GeneroMusical;
import br.com.carlos.artist_manager_api.domain.repository.GeneroMusicalRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GeneroControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private GeneroMusicalRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar gênero com sucesso")
    void deveCriarGenero() throws Exception {
        GeneroInput input = new GeneroInput("Pagode", "pagode");

        mockMvc.perform(post("/v1/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Pagode"))
                .andExpect(jsonPath("$.slug").value("pagode"));
    }

    @Test
    @DisplayName("Deve falhar ao criar gênero com slug duplicado")
    void deveFalharSlugDuplicado() throws Exception {
        GeneroMusical existente = new GeneroMusical();
        existente.setNome("Rock");
        existente.setSlug("rock");
        repository.save(existente);

        GeneroInput input = new GeneroInput("Rock Progressivo", "rock");

        mockMvc.perform(post("/v1/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Violação de Regra de Negócio"));
    }

    @Test
    @DisplayName("Deve falhar com erro de validação (campos vazios)")
    void deveFalharValidacaoCampos() throws Exception {
        GeneroInput input = new GeneroInput("", "");

        mockMvc.perform(post("/v1/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de Validação"));
    }

    @Test
    @DisplayName("Deve atualizar gênero com sucesso")
    void deveAtualizarGenero() throws Exception {
        GeneroMusical genero = new GeneroMusical();
        genero.setNome("Samba");
        genero.setSlug("samba");
        repository.save(genero);

        GeneroInput input = new GeneroInput("Samba Raiz", "samba-raiz");

        mockMvc.perform(put("/v1/generos/{id}", genero.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Samba Raiz"))
                .andExpect(jsonPath("$.slug").value("samba-raiz"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar gênero inexistente")
    void deveRetornar404AoAtualizarInexistente() throws Exception {
        GeneroInput input = new GeneroInput("Blues", "blues");

        mockMvc.perform(put("/v1/generos/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve falhar ao tentar atualizar para um slug que já pertence a outro gênero")
    void deveFalharAtualizacaoSlugDuplicado() throws Exception {
        GeneroMusical g1 = new GeneroMusical();
        g1.setNome("Rock");
        g1.setSlug("rock");
        repository.save(g1);

        GeneroMusical g2 = new GeneroMusical();
        g2.setNome("Pop");
        g2.setSlug("pop");
        repository.save(g2);

        GeneroInput input = new GeneroInput("Pop Rock", "rock");

        mockMvc.perform(put("/v1/generos/{id}", g2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Violação de Regra de Negócio"));
    }



    @Test
    @DisplayName("Deve listar todos os gêneros")
    void deveListarGeneros() throws Exception {
        GeneroMusical g1 = new GeneroMusical();
        g1.setNome("Jazz");
        g1.setSlug("jazz");
        repository.save(g1);

        GeneroMusical g2 = new GeneroMusical();
        g2.setNome("Blues");
        g2.setSlug("blues");
        repository.save(g2);

        mockMvc.perform(get("/v1/generos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").exists());
    }
}