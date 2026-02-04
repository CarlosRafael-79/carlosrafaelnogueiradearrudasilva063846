package br.com.carlos.artist_manager_api.api.controller;

import br.com.carlos.artist_manager_api.api.dto.AlbumInput;
import br.com.carlos.artist_manager_api.api.dto.AlbumResponse;
import br.com.carlos.artist_manager_api.domain.service.AlbumService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/albuns")
@RequiredArgsConstructor
@Tag(name = "Álbuns", description = "Gestão de álbuns, vinculação de artistas e upload de capas")
public class AlbumController {

    private final AlbumService service;

    @GetMapping
    @Operation(summary = "Listar álbuns (com filtros)", description = "Busca álbuns filtrando por título parcial ou nome do artista vinculado.")
    public ResponseEntity<Page<AlbumResponse>> listar(
            @Parameter(description = "Título do álbum (parcial)") @RequestParam(required = false) String titulo,
            @Parameter(description = "Nome do artista (parcial)") @RequestParam(required = false) String nomeArtista,
            @PageableDefault(sort = "anoLancamento", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.listar(titulo, nomeArtista, pageable));
    }

    @PostMapping
    @Operation(summary = "Cadastrar álbum", description = "Cria o registro do álbum e vincula artistas/gêneros (sem imagens).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Álbum criado"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou IDs de artistas/gêneros inexistentes", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<AlbumResponse> criar(@RequestBody @Valid AlbumInput input) {
        AlbumResponse response = service.salvar(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar álbum", description = "Atualiza metadados e relacionamentos do álbum.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Álbum atualizado"),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<AlbumResponse> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid AlbumInput input
    ) {
        AlbumResponse response = service.atualizar(id, input);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{id}/imagens", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de capas", description = "Envia uma ou mais imagens para a galeria do álbum.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Upload realizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado"),
            @ApiResponse(responseCode = "413", description = "Arquivo excede o tamanho máximo permitido", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> uploadImagens(
            @PathVariable UUID id,
            @Parameter(description = "Arquivos de imagem (.jpg, .png)")
            @RequestPart("imagens") List<MultipartFile> imagens
    ) {
        service.uploadImagens(id, imagens);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}