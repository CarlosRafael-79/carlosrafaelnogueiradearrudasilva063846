package br.com.carlos.artist_manager_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "album")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Album extends EntidadeBase{

    @Column(nullable = false)
    private String titulo;

    @Column(name = "ano_lancamento")
    private Integer anoLancamento;

    @Column(name = "duracao_segundos")
    private Integer duracaoSegundos;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ImagemAlbum> imagens = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "album_artista",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "artista_id")
    )
    private Set<Artista> artistas = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "album_genero",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private Set<GeneroMusical> generos = new HashSet<>();

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    public void adicionarImagem(ImagemAlbum imagem) {
        this.imagens.add(imagem);
        imagem.setAlbum(this);
    }
}