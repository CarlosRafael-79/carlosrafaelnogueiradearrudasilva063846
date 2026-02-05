package br.com.carlos.artist_manager_api.domain.service;

import br.com.carlos.artist_manager_api.api.dto.GeneroInput;
import br.com.carlos.artist_manager_api.api.dto.GeneroResponse;
import br.com.carlos.artist_manager_api.api.mapper.GeneroMapper;
import br.com.carlos.artist_manager_api.domain.entity.GeneroMusical;
import br.com.carlos.artist_manager_api.domain.repository.GeneroMusicalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneroServiceTest {

    @InjectMocks
    private GeneroService service;

    @Mock private GeneroMusicalRepository repository;
    @Mock private GeneroMapper mapper;

    @Test
    @DisplayName("Deve listar todos os gêneros corretamente")
    void deveListarTodos() {
        UUID id = UUID.randomUUID();

        GeneroMusical genero = new GeneroMusical();
        genero.setId(id);
        genero.setNome("Rock");
        genero.setSlug("rock");

        GeneroResponse response = new GeneroResponse(id, "Rock", "rock");

        when(repository.findAll()).thenReturn(List.of(genero));
        when(mapper.toResponse(genero)).thenReturn(response);

        List<GeneroResponse> resultado = service.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Rock", resultado.get(0).nome());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Deve salvar gênero com sucesso quando slug for único")
    void deveSalvarGeneroComSucesso() {
        GeneroInput input = new GeneroInput("Rock Alternativo", "rock-alt");

        GeneroMusical entidade = new GeneroMusical();
        entidade.setNome(input.nome());
        entidade.setSlug(input.slug());

        GeneroMusical entidadeSalva = new GeneroMusical();
        entidadeSalva.setId(UUID.randomUUID());
        entidadeSalva.setNome(input.nome());
        entidadeSalva.setSlug(input.slug());

        GeneroResponse responseEsperado = new GeneroResponse(
                entidadeSalva.getId(), "Rock Alternativo", "rock-alt"
        );

        when(repository.existsBySlug("rock-alt")).thenReturn(false);
        when(mapper.toEntity(input)).thenReturn(entidade);
        when(repository.save(any(GeneroMusical.class))).thenReturn(entidadeSalva);
        when(mapper.toResponse(entidadeSalva)).thenReturn(responseEsperado);

        GeneroResponse response = service.salvar(input);

        assertNotNull(response);
        assertEquals(input.slug(), response.slug());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar slug duplicado")
    void deveLancarErroSlugDuplicado() {
        GeneroInput input = new GeneroInput("Rock", "rock");
        when(repository.existsBySlug("rock")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(input));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar gênero com sucesso")
    void deveAtualizarGeneroComSucesso() {

        UUID id = UUID.randomUUID();
        GeneroInput input = new GeneroInput("Pop Rock", "pop-rock");

        GeneroMusical generoExistente = new GeneroMusical();
        generoExistente.setId(id);
        generoExistente.setNome("Pop");
        generoExistente.setSlug("pop-rock");

        GeneroMusical generoAtualizado = new GeneroMusical();
        generoAtualizado.setId(id);
        generoAtualizado.setNome("Pop Rock");
        generoAtualizado.setSlug("pop-rock");

        GeneroResponse responseEsperado = new GeneroResponse(id, "Pop Rock", "pop-rock");


        when(repository.findById(id)).thenReturn(Optional.of(generoExistente));

        when(repository.save(generoExistente)).thenReturn(generoAtualizado);
        when(mapper.toResponse(generoAtualizado)).thenReturn(responseEsperado);

        GeneroResponse resultado = service.atualizar(id, input);

        assertNotNull(resultado);
        assertEquals("Pop Rock", resultado.nome());
        verify(mapper).copyToEntity(input, generoExistente);
        verify(repository).save(generoExistente);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar gênero inexistente")
    void deveLancarErroAoAtualizarIdInexistente() {
        UUID id = UUID.randomUUID();
        GeneroInput input = new GeneroInput("Teste", "teste");

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.atualizar(id, input));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro ao atualizar slug para um que já existe em outro registro")
    void deveLancarErroAoAtualizarParaSlugDuplicado() {

        UUID id = UUID.randomUUID();

        GeneroInput input = new GeneroInput("Pop", "pop");

        GeneroMusical generoExistente = new GeneroMusical();
        generoExistente.setId(id);
        generoExistente.setSlug("rock");
        when(repository.findById(id)).thenReturn(Optional.of(generoExistente));

        when(repository.existsBySlug("pop")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.atualizar(id, input)
        );

        assertEquals("Já existe outro gênero com o slug: pop", ex.getMessage());
        verify(repository, never()).save(any());
    }
}