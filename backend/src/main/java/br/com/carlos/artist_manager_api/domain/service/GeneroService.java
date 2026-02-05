package br.com.carlos.artist_manager_api.domain.service;


import br.com.carlos.artist_manager_api.api.dto.GeneroInput;
import br.com.carlos.artist_manager_api.api.dto.GeneroResponse;
import br.com.carlos.artist_manager_api.api.mapper.GeneroMapper;
import br.com.carlos.artist_manager_api.domain.entity.GeneroMusical;
import br.com.carlos.artist_manager_api.domain.repository.GeneroMusicalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeneroService {

    private final GeneroMusicalRepository repository;
    private final GeneroMapper mapper;

    @Transactional(readOnly = true)
    public List<GeneroResponse> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public GeneroResponse salvar(GeneroInput input) {
        if (repository.existsBySlug(input.slug())) {
            throw new IllegalArgumentException("Já existe um gênero com o slug: " + input.slug());
        }

        GeneroMusical entity = mapper.toEntity(input);

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public GeneroResponse atualizar(UUID id, GeneroInput input) {
        GeneroMusical genero = repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Gênero não encontrado"));

        if (!genero.getSlug().equals(input.slug()) && repository.existsBySlug(input.slug())) {
            throw new IllegalArgumentException("Já existe outro gênero com o slug: " + input.slug());
        }

        mapper.copyToEntity(input, genero);

        return mapper.toResponse(repository.save(genero));
    }
}