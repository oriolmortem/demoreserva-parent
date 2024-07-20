package pe.bidea.development.service.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import pe.bidea.development.dto.Reserva;
import pe.bidea.development.persistence_ports.ReservaPersistence;
import pe.bidea.development.service.ReservaService;

@RequiredArgsConstructor
@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaPersistence reservaPersistence;
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final Logger log = LogManager.getLogger(ReservaServiceImpl.class);
    private static final int MAX_RETRIES = 3;
    private static final long TIMEOUT_MS = 5000; // 5 seconds

    @Override
    public void book(Reserva r) throws Exception {
        try {
            if (validateRequest(r)) {
                if (validateDiscountCode(r)) {
                    r.setCreationTime(LocalDateTime.now());
                    reservaPersistence.save(r);
                } else {
                    throw new Exception("CONFLICT: Invalid discount code.");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    public boolean validateRequest(Reserva r) throws Exception {

        if (r.getId().trim().isEmpty()) {
            throw new Exception("ERROR:ID usuario no válido");
        }

        if (r.getName().isEmpty() || r.getLastName().isEmpty() || r.getAge().toString().isEmpty() || !(r.getAge() > 0)
                || r.getPhoneNumber().trim().isEmpty()) {
            throw new Exception("ERROR:Verifique datos obligatorios (nombre, apellidos, edad, teléfono)");
        }

        if (r.getStartDate().toString().isEmpty() || r.getEndDate().toString().isEmpty()) {
            throw new Exception("ERROR:Fechas inicio/fin de reserva no válidos");
        }

        if (r.getStartDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new Exception("ERROR:Fecha de inicio de reserva no debe ser anterior a la fecha actual.");
        }

        if (r.getEndDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new Exception("ERROR:Fecha de fin de reserva no debe ser anterior a la fecha actual.");
        }

        if (!(r.getStartDate().isBefore(r.getEndDate()) || r.getStartDate().isEqual(r.getEndDate()))) {
            throw new Exception(
                    "ERROR:Fecha de fin de reserva no debe ser anterior a la fecha inicio de reserva.");
        }

        if (r.getHouseId() == 0) {
            throw new Exception(
                    "ERROR:Código de casa en reserva es obligatorio.");
        }
        return true;
    }

    private boolean validateDiscountCode(Reserva r) throws Exception {
        HttpStatusCode statusCode = null;
        boolean responseStatusCode = false;

        if (r.getDiscountCode().trim().isEmpty())
            return true;

        String url = "https://sbv2bumkomidlxwffpgbh4k6jm0ydskh.lambda-url.us-east-1.on.aws/";

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Request body
        String jsonBody = "{ \"userId\": \"" + r.getId()
                + "\", \"houseId\": \"" + r.getHouseId() + "\", \"discountCode\": \"" + r.getDiscountCode()
                + "\" }";
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
                    @Override
                    protected void prepareConnection(HttpURLConnection connection, String method)
                            throws IOException {
                        connection.setConnectTimeout((int) TIMEOUT_MS);
                        connection.setReadTimeout((int) TIMEOUT_MS);
                        super.prepareConnection(connection, method);
                    }
                });

                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                        String.class);
                statusCode = responseEntity.getStatusCode();
                String responseBody = responseEntity.getBody();
                JSONObject jsonObject = new JSONObject(responseBody);
                responseStatusCode = jsonObject.getBoolean("status");

                if (statusCode == HttpStatus.OK && responseStatusCode) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                attempt++;
                if (attempt == MAX_RETRIES) {
                    throw new Exception("CONFLICT: Max retries reached.");
                }
            }
        }
        return false;
    }
}
