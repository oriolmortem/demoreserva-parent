import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pe.bidea.development.dto.Reserva;
import pe.bidea.development.persistence_ports.ReservaPersistence;
import pe.bidea.development.service.ReservaService;
import pe.bidea.development.service.impl.ReservaServiceImpl;

public class ReservaServiceTest {

    @Mock
    private ReservaPersistence reservaPersistence;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBook_ValidReservation() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setId("1");
        reserva.setName("John");
        reserva.setLastName("Doe");
        reserva.setAge(25);
        reserva.setPhoneNumber("1234567890");
        reserva.setStartDate(LocalDateTime.now().plusDays(1).toLocalDate());
        reserva.setEndDate(LocalDateTime.now().plusDays(2).toLocalDate());
        reserva.setHouseId(14564L);
        reserva.setDiscountCode("DISCOUNT10");

        // Mock validateDiscountCode response
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"status\": true}", HttpStatus.OK));

        // verify(reservaPersistence, times(1)).save(reserva);
    }

    @Test
    void testBook_InvalidDiscountCode() {
        Reserva reserva = new Reserva();
        reserva.setId("1");
        reserva.setName("John");
        reserva.setLastName("Doe");
        reserva.setAge(25);
        reserva.setPhoneNumber("1234567890");
        reserva.setStartDate(LocalDateTime.now().plusDays(1).toLocalDate());
        reserva.setEndDate(LocalDateTime.now().plusDays(2).toLocalDate());
        reserva.setHouseId(12333L);
        reserva.setDiscountCode("DISCOUNT10");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"status\": true}", HttpStatus.OK));
    }

    @Test
    void testValidateRequest_ValidRequest() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setId("1");
        reserva.setName("John");
        reserva.setLastName("Doe");
        reserva.setAge(25);
        reserva.setPhoneNumber("1234567890");
        reserva.setStartDate(LocalDateTime.now().plusDays(1).toLocalDate());
        reserva.setEndDate(LocalDateTime.now().plusDays(2).toLocalDate());
        reserva.setHouseId(12222L);

        assertTrue(reservaService.validateRequest(reserva));
    }

    @Test
    void testValidateRequest_InvalidRequest() {
        Reserva reserva = new Reserva();
        reserva.setId("");
        reserva.setName("");
        reserva.setLastName("");
        reserva.setAge(0);
        reserva.setPhoneNumber("");
        reserva.setStartDate(LocalDateTime.now().minusDays(1).toLocalDate());
        reserva.setEndDate(LocalDateTime.now().minusDays(1).toLocalDate());
        reserva.setHouseId(0232L);

        Exception exception = assertThrows(Exception.class, () -> {
            reservaService.validateRequest(reserva);
        });

        assertTrue(exception.getMessage().contains("ERROR:ID usuario no válido"));
    }
}