package edu.utn.frsf.isi.dan.reservas_svc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarifa {
    private Double precio;
    private String moneda;
}
