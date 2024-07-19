package pe.bidea.development.adapters.postgres.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import pe.bidea.development.adapters.postgres.daos.ReservaRepository;
import pe.bidea.development.adapters.postgres.entities.ReservaEntity;
import pe.bidea.development.dto.Reserva;
import pe.bidea.development.persistence_ports.ReservaPersistence;

@RequiredArgsConstructor
@Repository("brandPersistence")
public class ReservaPersistencePostgres implements ReservaPersistence {
    private static final Logger log = LogManager.getLogger(ReservaPersistencePostgres.class);

    private final ReservaRepository reservaRepository;

    @Override
    public void save(Reserva reserva) throws Exception {
        try {
            ReservaEntity reservaEntity = toEntity(reserva);
            reservaRepository.save(reservaEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    private ReservaEntity toEntity(Reserva reserva) {
		ReservaEntity reservaEntity = new ReservaEntity();
        reservaEntity.setAge(reserva.getAge());
        reservaEntity.setCreationTime(reserva.getCreationTime());
        reservaEntity.setDeleteTime(reserva.getDeleteTime());
        reservaEntity.setDiscountCode(reserva.getDiscountCode());
        reservaEntity.setEndDate(reserva.getEndDate());
        reservaEntity.setHouseId(reserva.getHouseId());
        reservaEntity.setId(reserva.getId());
        reservaEntity.setLastName(reserva.getLastName());
        reservaEntity.setName(reserva.getName());
        reservaEntity.setPhoneNumber(reserva.getPhoneNumber());
        reservaEntity.setStartDate(reserva.getStartDate());
        reservaEntity.setUpdateTime(reserva.getUpdateTime());
		return reservaEntity;
	}
}