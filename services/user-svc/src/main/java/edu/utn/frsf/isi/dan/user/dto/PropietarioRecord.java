package edu.utn.frsf.isi.dan.user.dto;

import edu.utn.frsf.isi.dan.user.model.Propietario;
import edu.utn.frsf.isi.dan.user.model.CuentaBancaria;

public record PropietarioRecord(
    String nombre,
    String email,
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