package edu.utn.frsf.isi.dan.shared;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarifaDTO {
    private Integer tipoHabitacionId;
    private Double nuevoPrecio;
}
