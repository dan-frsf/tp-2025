package edu.utn.frsf.isi.dan.user.dto;

import edu.utn.frsf.isi.dan.user.model.Banco;
import edu.utn.frsf.isi.dan.user.model.CuentaBancaria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CuentaBancariaRecord(
    @NotBlank(message = "El número de cuenta no puede estar vacío")
    String numeroCuenta,
    @NotBlank(message = "El número de cuenta no puede estar vacío")
    String cbu,
    @NotBlank(message = "El número de cuenta no puede estar vacío")
    String alias,
    @NotNull(message = "El banco no puede estar vacío")
    Integer idBanco
) {
    public CuentaBancaria toCuentaBancaria() {
        CuentaBancaria cuentaBancaria = new CuentaBancaria();
        cuentaBancaria.setNumeroCuenta(this.numeroCuenta);
        cuentaBancaria.setCbu(this.cbu);
        cuentaBancaria.setAlias(this.alias);
        cuentaBancaria.setBanco(Banco.builder()
            .id(this.idBanco)
            .build());
        // El banco debe ser configurado por separado
        return cuentaBancaria;
    }
}