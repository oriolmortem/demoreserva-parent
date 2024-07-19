package pe.bidea.development.adapters.postgres.daos;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.bidea.development.adapters.postgres.entities.ReservaEntity;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Integer> {

}
