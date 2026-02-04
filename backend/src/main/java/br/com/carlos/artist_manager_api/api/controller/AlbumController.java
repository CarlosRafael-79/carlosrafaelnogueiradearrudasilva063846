package br.com.carlos.artist_manager_api.api.controller;

import br.com.carlos.artist_manager_api.api.dto.AlbumInput;
import br.com.carlos.artist_manager_api.api.dto.AlbumResponse;
import br.com.carlos.artist_manager_api.domain.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/albuns")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService service;


    @GetMapping
    public ResponseEntity<Page<AlbumResponse>> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String nomeArtista,
            @PageableDefault(sort = "anoLancamento", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok(service.listar(titulo, nomeArtista, pageable));
    }

    @PostMapping
    public ResponseEntity<AlbumResponse> criar(@RequestBody @Valid AlbumInput input) {
        AlbumResponse response = service.salvar(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponse> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid AlbumInput input
    ) {
        AlbumResponse response = service.atualizar(id, input);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{id}/imagens", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadImagens(
            @PathVariable UUID id,
            @RequestPart("imagens") List<MultipartFile> imagens
    ) {
        service.uploadImagens(id, imagens);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}