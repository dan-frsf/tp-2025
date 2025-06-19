package edu.utn.frsf.isi.dan.shared;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDTO {
        private Integer id;
        private String nombre;
        private String cuit;
        private String domicilio;
        private Double latitud;
        private Double longitud;
        private String telefono;
        private String correoContacto;
        private Integer categoria;
   
}
