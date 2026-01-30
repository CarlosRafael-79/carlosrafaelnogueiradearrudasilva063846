package br.com.carlos.artist_manager_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "artista")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Artista extends EntidadeBase{

    @Column(nullable = false)
    private String nome;

    @Column(name = "pais_origem", length = 3)
    private String paisOrigem;

    @Column(name = "ano_formacao")
    private Integer anoFormacao;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;
}