package edu.utn.frsf.isi.dan.gestion.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_habitacion", schema = "tp_dan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoHabitacion {
    @Id
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer capacidad;
}
