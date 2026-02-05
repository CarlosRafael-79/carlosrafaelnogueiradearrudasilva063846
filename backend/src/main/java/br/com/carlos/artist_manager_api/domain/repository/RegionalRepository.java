package br.com.carlos.artist_manager_api.domain.repository;

import br.com.carlos.artist_manager_api.domain.entity.Regional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface RegionalRepository extends JpaRepository<Regional, UUID> {
    List<Regional> findByAtivoTrue();
}