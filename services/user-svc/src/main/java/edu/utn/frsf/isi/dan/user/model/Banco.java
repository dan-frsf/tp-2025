package edu.utn.frsf.isi.dan.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

// anotaciones de jpa
@Entity
@Table(name = "bancos")
// anotaciones de lombok
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
}
