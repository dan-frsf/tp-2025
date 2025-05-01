package edu.utn.frsf.isi.dan.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "tarjetas_credito")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaCredito {

    @Id
    private String numero;

    @Column(name = "nombre_titular")
    private String nombreTitular;
    @Column(name = "fecha_vencimiento")    
    private String fechaVencimiento;
    @Column(name = "codigo_seguridad")    
    private String cvc;
    @Column(name = "es_principal")    
    private Boolean esPrincipal;

    @ManyToOne
    @JoinColumn(name = "banco_id")
    private Banco banco;

    @ManyToOne
    @JoinColumn(name = "huesped_id")
    @JsonIgnore
    private Huesped huesped;

}
