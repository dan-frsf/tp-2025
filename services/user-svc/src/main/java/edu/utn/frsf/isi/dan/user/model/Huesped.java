package edu.utn.frsf.isi.dan.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@DiscriminatorValue("HUESPED")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Huesped extends Usuario {

    private LocalDate fechaNacimiento;
    @OneToMany(mappedBy = "huesped")
    private List<TarjetaCredito> tarjetaCredito;

}
