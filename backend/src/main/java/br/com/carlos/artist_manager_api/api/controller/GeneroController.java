package br.com.carlos.artist_manager_api.api.controller;

import br.com.carlos.artist_manager_api.api.dto.GeneroInput;
import br.com.carlos.artist_manager_api.api.dto.GeneroResponse;
import br.com.carlos.artist_manager_api.domain.service.GeneroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/generos")
@RequiredArgsConstructor
@Tag(name = "Gêneros", description = "Gerenciamento de gêneros musicais")
public class GeneroController {

    private final GeneroService service;

    @GetMapping
    @Operation(summary = "Listar todos os gêneros", description = "Retorna a lista completa sem paginação.")
    public ResponseEntity<List<GeneroResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo gênero")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Gênero criado"),
            @ApiResponse(responseCode = "400", description = "Slug duplicado ou dados inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<GeneroResponse> criar(@RequestBody @Valid GeneroInput input) {
        GeneroResponse response = service.salvar(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar gênero existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Gênero não encontrado")
    })
    public ResponseEntity<GeneroResponse> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid GeneroInput input
    ) {
        GeneroResponse response = service.atualizar(id, input);
        return ResponseEntity.ok(response);
    }
}