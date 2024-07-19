package pe.bidea.development.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String lastName;

    private Integer age;

    private String phoneNumber;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long houseId;

    private String discountCode;

    private LocalDateTime creationTime;

    private LocalDateTime updateTime;

    private LocalDateTime deleteTime;
}
