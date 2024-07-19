package pe.bidea.development.persistence_ports;

import org.springframework.stereotype.Repository;

import pe.bidea.development.dto.Reserva;

@Repository
public interface ReservaPersistence {

	public void save(Reserva a) throws Exception;
}
