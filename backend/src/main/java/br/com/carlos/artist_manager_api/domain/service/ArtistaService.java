package br.com.carlos.artist_manager_api.domain.service;

import br.com.carlos.artist_manager_api.api.dto.ArtistaInput;
import br.com.carlos.artist_manager_api.api.dto.ArtistaResponse;
import br.com.carlos.artist_manager_api.api.mapper.ArtistaMapper;
import br.com.carlos.artist_manager_api.domain.entity.Artista;
import br.com.carlos.artist_manager_api.domain.repository.ArtistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistaService {

    private final ArtistaRepository repository;
    private final ArtistaMapper mapper;

    @Transactional(readOnly = true)
    public Page<ArtistaResponse> listar(String filtroNome, Pageable pageable) {
        Page<Artista> pagina;

        if (filtroNome != null && !filtroNome.isBlank()) {
            pagina = repository.findByNomeContainingIgnoreCase(filtroNome, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }

        return pagina.map(mapper::toResponse);
    }

    @Transactional
    public ArtistaResponse salvar(ArtistaInput input) {
        Artista artista = mapper.toEntity(input);

        artista = repository.save(artista);

        return mapper.toResponse(artista);
    }

    @Transactional
    public ArtistaResponse atualizar(UUID id, ArtistaInput input) {
        Artista artista = repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Artista não encontrado"));

        mapper.copyToEntity(input, artista);

        return mapper.toResponse(repository.save(artista));
    }
}