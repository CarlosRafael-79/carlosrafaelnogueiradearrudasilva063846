package br.com.carlos.artist_manager_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "regional")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Regional {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "id_externo", nullable = false)
    private Integer codigo;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(name = "data_sincronizacao")
    private LocalDateTime dataSincronizacao;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.dataSincronizacao == null) {
            this.dataSincronizacao = LocalDateTime.now();
        }
    }
}