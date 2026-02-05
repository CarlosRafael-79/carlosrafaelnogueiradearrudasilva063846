package br.com.carlos.artist_manager_api.api.controller;

import br.com.carlos.artist_manager_api.domain.service.RegionalSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/regionais")
@RequiredArgsConstructor
public class RegionalController {

    private final RegionalSyncService syncService;


    @PostMapping("/sincronizacao")
    public ResponseEntity<Void> sincronizar() {
        syncService.sincronizar();
        return ResponseEntity.noContent().build();
    }
}