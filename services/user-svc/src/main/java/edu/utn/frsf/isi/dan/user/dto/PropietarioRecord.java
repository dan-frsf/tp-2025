package edu.utn.frsf.isi.dan.user.dto;

import edu.utn.frsf.isi.dan.user.model.Propietario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import edu.utn.frsf.isi.dan.user.model.CuentaBancaria;

public record PropietarioRecord(
    @NotBlank(message = "El nombre no puede estar vacío") 
    @Length(min = 5, message = "El nombre no puede tener menos de 5 caracteres")  
    String nombre,
    @Email(message = "El email no es válido")
    String email,
    @NotBlank(message = "El teléfono no puede estar vacío")
    String telefono,    
    Long idHotel,
    CuentaBancariaRecord cuentaBancaria
) {
    public Propietario toPropietario() {
        Propietario propietario = new Propietario();
        propietario.setNombre(this.nombre);
        propietario.setEmail(this.email);
        propietario.setTelefono(this.telefono);        
        CuentaBancaria cuentaBancaria = this.cuentaBancaria.toCuentaBancaria();
        propietario.setCuentaBancaria(cuentaBancaria);
        return propietario;
    }
}