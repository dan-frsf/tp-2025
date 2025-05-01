package edu.utn.frsf.isi.dan.user.dto;

import edu.utn.frsf.isi.dan.user.model.Banco;
import edu.utn.frsf.isi.dan.user.model.CuentaBancaria;

public record CuentaBancariaRecord(
    String numeroCuenta,
    String cbu,
    String alias,
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