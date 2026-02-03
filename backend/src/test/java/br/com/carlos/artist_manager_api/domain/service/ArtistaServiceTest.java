package br.com.carlos.artist_manager_api.domain.service;

import br.com.carlos.artist_manager_api.api.dto.ArtistaInput;
import br.com.carlos.artist_manager_api.api.dto.ArtistaResponse;
import br.com.carlos.artist_manager_api.api.mapper.ArtistaMapper;
import br.com.carlos.artist_manager_api.domain.entity.Artista;
import br.com.carlos.artist_manager_api.domain.repository.ArtistaRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistaServiceTest {

    @InjectMocks
    private ArtistaService service;

    @Mock private ArtistaRepository repository;
    @Mock private ArtistaMapper mapper;

    @Test
    @DisplayName("Deve converter DTO e salvar artista corretamente")
    void deveSalvarArtista() {

        ArtistaInput input = new ArtistaInput("System of a Down", "USA", 1994, "Metal");

        Artista entidade = new Artista();
        entidade.setNome("System of a Down");

        Artista entidadeSalva = new Artista();
        entidadeSalva.setId(UUID.randomUUID());
        entidadeSalva.setNome("System of a Down");

        ArtistaResponse responseEsperado = new ArtistaResponse(
                entidadeSalva.getId(), "System of a Down", "USA", 1994, "Metal"
        );


        when(mapper.toEntity(input)).thenReturn(entidade);
        when(repository.save(any(Artista.class))).thenReturn(entidadeSalva);
        when(mapper.toResponse(entidadeSalva)).thenReturn(responseEsperado);


        ArtistaResponse response = service.salvar(input);

        assertNotNull(response.id());
        assertEquals("System of a Down", response.nome());
        verify(repository, times(1)).save(entidade);
    }



    @Test
    @DisplayName("Deve atualizar artista com sucesso quando ID existe")
    void deveAtualizarArtistaComSucesso() {
        UUID id = UUID.randomUUID();
        ArtistaInput input = new ArtistaInput("Nome Editado", "BRA", 2000, "Desc");

        Artista artistaExistente = new Artista();
        artistaExistente.setId(id);
        artistaExistente.setNome("Nome Antigo");

        Artista artistaAtualizado = new Artista();
        artistaAtualizado.setId(id);
        artistaAtualizado.setNome("Nome Editado");

        ArtistaResponse responseEsperado = new ArtistaResponse(id, "Nome Editado", "BRA", 2000, "Desc");

        when(repository.findById(id)).thenReturn(Optional.of(artistaExistente));

        when(repository.save(artistaExistente)).thenReturn(artistaAtualizado);
        when(mapper.toResponse(artistaAtualizado)).thenReturn(responseEsperado);


        ArtistaResponse resultado = service.atualizar(id, input);


        assertNotNull(resultado);
        assertEquals("Nome Editado", resultado.nome());

        verify(mapper).copyToEntity(input, artistaExistente);
        verify(repository).save(artistaExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar artista inexistente")
    void deveLancarErroAoAtualizarIdInexistente() {
        UUID id = UUID.randomUUID();
        ArtistaInput input = new ArtistaInput("Teste", "USA", 2000, "Desc");

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.atualizar(id, input));

        verify(repository, never()).save(any());
        verify(mapper, never()).copyToEntity(any(), any());
    }


    @Test
    @DisplayName("Deve listar todos (findAll) quando filtroNome for nulo")
    void deveListarTodosSemFiltro() {
        Pageable pageable = PageRequest.of(0, 10);
        Artista artista = new Artista();
        Page<Artista> paginaBanco = new PageImpl<>(List.of(artista));
        ArtistaResponse responseDto = new ArtistaResponse(UUID.randomUUID(), "Banda", "USA", 1990, "Desc");

        when(repository.findAll(pageable)).thenReturn(paginaBanco);
        when(mapper.toResponse(artista)).thenReturn(responseDto);

        Page<ArtistaResponse> resultado = service.listar(null, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(repository).findAll(pageable);
        verify(repository, never()).findByNomeContainingIgnoreCase(any(), any());
    }

    @Test
    @DisplayName("Deve filtrar por nome (findByNome) quando filtroNome for preenchido")
    void deveListarComFiltroDeNome() {
        String filtro = "Guns";
        Pageable pageable = PageRequest.of(0, 10);
        Artista artista = new Artista();
        Page<Artista> paginaBanco = new PageImpl<>(List.of(artista));
        ArtistaResponse responseDto = new ArtistaResponse(UUID.randomUUID(), "Guns N Roses", "USA", 1985, "Hard Rock");

        when(repository.findByNomeContainingIgnoreCase(filtro, pageable)).thenReturn(paginaBanco);
        when(mapper.toResponse(artista)).thenReturn(responseDto);

        Page<ArtistaResponse> resultado = service.listar(filtro, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(repository).findByNomeContainingIgnoreCase(filtro, pageable);
        verify(repository, never()).findAll(any(Pageable.class));
    }
}