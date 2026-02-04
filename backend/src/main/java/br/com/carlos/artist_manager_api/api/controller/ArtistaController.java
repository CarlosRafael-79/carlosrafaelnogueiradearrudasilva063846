package br.com.carlos.artist_manager_api.api.controller;

import br.com.carlos.artist_manager_api.api.dto.ArtistaInput;
import br.com.carlos.artist_manager_api.api.dto.ArtistaResponse;
import br.com.carlos.artist_manager_api.domain.service.ArtistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
    @Operation(summary = "Listar artistas", description = "Retorna uma lista paginada de artistas com opção de filtro por nome.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Page<ArtistaResponse>> listar(
            @Parameter(description = "Filtro parcial pelo nome do artista (opcional)")
            @RequestParam(required = false) String nome,

            @Parameter(description = "Paginação (ex: page=0, size=10, sort=nome,asc)")
            @PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.listar(nome, pageable));
    }

    @PostMapping
    @Operation(summary = "Cadastrar artista", description = "Cria um novo registro de artista no banco de dados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artista criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ArtistaResponse> criar(@RequestBody @Valid ArtistaInput input) {
        ArtistaResponse response = service.salvar(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar artista", description = "Atualiza os dados de um artista existente pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ArtistaResponse> atualizar(
            @Parameter(description = "ID único do artista") @PathVariable UUID id,
            @RequestBody @Valid ArtistaInput input
    ) {
        ArtistaResponse response = service.atualizar(id, input);
        return ResponseEntity.ok(response);
    }
}