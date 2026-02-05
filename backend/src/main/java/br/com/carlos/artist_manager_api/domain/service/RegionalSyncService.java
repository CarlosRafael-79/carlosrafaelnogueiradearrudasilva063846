package br.com.carlos.artist_manager_api.domain.service;

import br.com.carlos.artist_manager_api.api.dto.RegionalExternaDto;
import br.com.carlos.artist_manager_api.domain.entity.Regional;
import br.com.carlos.artist_manager_api.domain.repository.RegionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionalSyncService {

    private final RegionalRepository repository;
    private final RestClient restClient = RestClient.create();

    @Transactional
    public void sincronizar() {
        RegionalExternaDto[] respostaApi = restClient.get()
                .uri("https://integrador-argus-api.geia.vip/v1/regionais")
                .retrieve()
                .body(RegionalExternaDto[].class);

        if (respostaApi == null) return;

        Map<Integer, Regional> atuaisMap = repository.findByAtivoTrue().stream()
                .collect(Collectors.toMap(Regional::getCodigo, Function.identity()));

        List<Regional> paraSalvar = new ArrayList<>();

        for (RegionalExternaDto dto : respostaApi) {
            Regional atual = atuaisMap.get(dto.id());

            if (atual == null) {
                paraSalvar.add(criarNovo(dto));
            } else {
                atuaisMap.remove(dto.id());

                if (!atual.getNome().equals(dto.nome())) {
                    atual.setAtivo(false);
                    atual.setDataSincronizacao(LocalDateTime.now());
                    paraSalvar.add(atual);
                    paraSalvar.add(criarNovo(dto));
                }
            }
        }

        for (Regional regionalAusente : atuaisMap.values()) {
            regionalAusente.setAtivo(false);
            paraSalvar.add(regionalAusente);
        }

        repository.saveAll(paraSalvar);
    }

    private Regional criarNovo(RegionalExternaDto dto) {
        return Regional.builder()
                .codigo(dto.id())
                .nome(dto.nome())
                .ativo(true)
                .dataSincronizacao(LocalDateTime.now())
                .build();
    }
}