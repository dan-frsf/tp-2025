package edu.utn.frsf.isi.dan.shared;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitacionEvent {
        private HabitacionDTO habitacion;
        private TarifaDTO tarifa;
        private TipoEvento tipoEvento;

}
