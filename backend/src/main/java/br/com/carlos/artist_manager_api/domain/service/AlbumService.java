package br.com.carlos.artist_manager_api.domain.service;

import br.com.carlos.artist_manager_api.api.dto.*;
import br.com.carlos.artist_manager_api.api.mapper.AlbumMapper;
import br.com.carlos.artist_manager_api.domain.entity.Album;
import br.com.carlos.artist_manager_api.domain.entity.ImagemAlbum;
import br.com.carlos.artist_manager_api.domain.repository.AlbumRepository;
import br.com.carlos.artist_manager_api.domain.repository.AlbumSpecs;
import br.com.carlos.artist_manager_api.domain.repository.ArtistaRepository;
import br.com.carlos.artist_manager_api.domain.repository.GeneroMusicalRepository;
import br.com.carlos.artist_manager_api.infrastructure.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistaRepository artistaRepository;
    private final GeneroMusicalRepository generoRepository;
    private final FileStorageService storageService;
    private final AlbumMapper albumMapper;

    @Transactional(readOnly = true)
    public Page<AlbumResponse> listar(String titulo, String nomeArtista, Pageable pageable) {
        return albumRepository.findAll(AlbumSpecs.comFiltros(titulo, nomeArtista), pageable)
                .map(album -> albumMapper.toResponse(album, storageService));
    }

    @Transactional
    public AlbumResponse salvar(AlbumInput input) {
        Album album = albumMapper.toEntity(input);

        if (input.artistasIds() != null && !input.artistasIds().isEmpty()) {
            album.setArtistas(new HashSet<>(artistaRepository.findAllById(input.artistasIds())));
        }
        if (input.generosIds() != null && !input.generosIds().isEmpty()) {
            album.setGeneros(new HashSet<>(generoRepository.findAllById(input.generosIds())));
        }

        album = albumRepository.save(album);
        return albumMapper.toResponse(album, storageService);
    }

    @Transactional
    public AlbumResponse atualizar(UUID id, AlbumInput input) {
        Album albumExistente = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Álbum não encontrado"));

        albumMapper.copyToEntity(input, albumExistente);

        if (input.artistasIds() != null) {
            albumExistente.setArtistas(new java.util.HashSet<>(artistaRepository.findAllById(input.artistasIds())));
        }
        if (input.generosIds() != null) {
            albumExistente.setGeneros(new java.util.HashSet<>(generoRepository.findAllById(input.generosIds())));
        }

        return albumMapper.toResponse(albumRepository.save(albumExistente), storageService);
    }

    @Transactional
    public void uploadImagens(UUID albumId, java.util.List<MultipartFile> arquivos) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Álbum não encontrado"));

        for (MultipartFile arquivo : arquivos) {
            String extensao = getExtensao(arquivo.getOriginalFilename());
            String nomeArquivoStorage = UUID.randomUUID() + "." + extensao;

            try {
                storageService.upload(nomeArquivoStorage, arquivo.getInputStream(), arquivo.getContentType());
            } catch (Exception e) {
                throw new RuntimeException("Falha ao processar upload", e);
            }

            ImagemAlbum imagem = new ImagemAlbum();
            imagem.setNomeArquivo(arquivo.getOriginalFilename());
            imagem.setUrl(nomeArquivoStorage);

            album.adicionarImagem(imagem);
        }

        albumRepository.save(album);
    }

    private String getExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) return "jpg";
        return nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1);
    }
}