package pe.bidea.development.adapters.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import pe.bidea.development.dto.Reserva;
import pe.bidea.development.service.ReservaService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/book")
public class ReservaResource {
    private final Logger log = LoggerFactory.getLogger(ReservaResource.class);

    private final ReservaService reservaService;

    @PostMapping(path = "/")
    public ResponseEntity<String> book(@RequestBody Reserva reserva) throws Exception {
        try {
            reservaService.book(reserva);
            return ResponseEntity.ok("Book accepted.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e.getMessage().startsWith("ERROR")) {
                ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", e.getMessage());
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ObjectMapper().writeValueAsString(errorResponse));
            } else {
                ErrorResponse errorResponse = new ErrorResponse(400, "Conflict", e.getMessage());
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new ObjectMapper().writeValueAsString(errorResponse));
            }
        }
    }
}
