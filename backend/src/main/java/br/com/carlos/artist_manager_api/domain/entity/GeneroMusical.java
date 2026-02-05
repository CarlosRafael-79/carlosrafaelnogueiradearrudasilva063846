package br.com.carlos.artist_manager_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genero_musical")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class GeneroMusical extends EntidadeBase{

    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @Column(nullable = false, unique = true, length = 50)
    private String slug;
}