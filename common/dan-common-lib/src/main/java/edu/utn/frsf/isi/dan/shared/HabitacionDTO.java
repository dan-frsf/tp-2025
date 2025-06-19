package edu.utn.frsf.isi.dan.shared;
import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitacionDTO {
    
    private Long habitacionId;
    private Integer numero;
    private Integer piso;
    private Integer tipoHabitacionId;
    private Integer capacidad;
    private String tipoHabitacion;
    private String tipoHabitacionDescripcion;
    private Double precioNoche;
    private List<String> amenities;
    private HotelDTO hotel;

}
