package br.com.carlos.artist_manager_api.api.controller;

import br.com.carlos.artist_manager_api.api.dto.ArtistaInput;
import br.com.carlos.artist_manager_api.api.dto.ArtistaResponse;
import br.com.carlos.artist_manager_api.domain.service.ArtistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/artistas")
@RequiredArgsConstructor
@Tag(name = "Artistas", description = "Gerenciamento de artistas e bandas")
public class ArtistaController {

    private final ArtistaService service;

    @GetMapping
    @Operation(summary = "Listar artistas", description = "Lista paginada com opção de filtro por nome")
    public ResponseEntity<Page<ArtistaResponse>> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.listar(nome, pageable));
    }

    @PostMapping
    @Operation(summary = "Cadastrar artista")
    public ResponseEntity<ArtistaResponse> criar(@RequestBody @Valid ArtistaInput input) {
        ArtistaResponse response = service.salvar(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar artista")
    public ResponseEntity<ArtistaResponse> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid ArtistaInput input
    ) {
        ArtistaResponse response = service.atualizar(id, input);
        return ResponseEntity.ok(response);
    }

}