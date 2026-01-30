package br.com.carlos.artist_manager_api.domain.repository;

import br.com.carlos.artist_manager_api.domain.entity.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, UUID> {

}