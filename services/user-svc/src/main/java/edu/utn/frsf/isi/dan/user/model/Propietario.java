package edu.utn.frsf.isi.dan.user.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("PROPIETARIO")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Propietario extends Usuario {

    @OneToOne
    @JoinColumn(name = "cuenta_bancaria_id")
    private CuentaBancaria cuentaBancaria;

    @Column(name = "hotel_id")
    private Long idHotel;

    // Getters y setters...
}
