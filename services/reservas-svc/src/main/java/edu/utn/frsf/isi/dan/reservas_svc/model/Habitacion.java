package edu.utn.frsf.isi.dan.reservas_svc.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "habitacion")
public class Habitacion {
    @Id
    private String id;
    private Long habitacionId;
    private Integer capacidad;
    private Double precioNoche;
    private List<String> amenities;
    private List<ReservaSimple> reservas;
    private Hotel hotel;
    private Integer idTipoHabitacion;
    private String tipoHabitacion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReservaSimple {
        private String _id;
        private Instant checkIn;
        private Instant checkOut;
        private Double precioTotal;
        private EstadoReserva estadoReserva;
    }
}
