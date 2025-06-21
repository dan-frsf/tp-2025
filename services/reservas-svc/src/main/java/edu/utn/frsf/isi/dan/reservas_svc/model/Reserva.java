package edu.utn.frsf.isi.dan.reservas_svc.model;

import lombok.*;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "reserva")
public class Reserva {
    @Id
    private String _id;
    private String idHabitacion;
    private Long hotelId;
    private Instant createdAt;
    private Instant checkIn;
    private Instant checkOut;
    private Double precioNoche;
    private Double precioTotal;
    private String status;
    private Huesped huesped;
    private List<Pago> pago;
    private Review clientReview;
    private Review hostReview;
    private EstadoReserva estadoReserva;

}
