package br.com.carlos.artist_manager_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "imagem_album")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ImagemAlbum extends EntidadeBase{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
}