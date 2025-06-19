package edu.utn.frsf.isi.dan.user.service;

import edu.utn.frsf.isi.dan.user.dao.BancoRepository;
import edu.utn.frsf.isi.dan.user.dao.CuentaBancariaRepository;
import edu.utn.frsf.isi.dan.user.dao.TarjetaCreditoRepository;
import edu.utn.frsf.isi.dan.user.dao.UsuarioRepository;
import edu.utn.frsf.isi.dan.user.dto.HuespedRecord;
import edu.utn.frsf.isi.dan.user.dto.PropietarioRecord;
import edu.utn.frsf.isi.dan.user.model.Banco;
import edu.utn.frsf.isi.dan.user.model.CuentaBancaria;
import edu.utn.frsf.isi.dan.user.model.Huesped;
import edu.utn.frsf.isi.dan.user.model.Propietario;
import edu.utn.frsf.isi.dan.user.model.TarjetaCredito;
import edu.utn.frsf.isi.dan.user.model.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private BancoRepository bancoRepository;

    @Autowired 
    private CuentaBancariaRepository cuentaBancariaRepository;

    @Autowired
    private TarjetaCreditoRepository tarjetaCreditoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Huesped crearUsuarioHuesped(HuespedRecord huespedRecord) {
        // Buscar el banco por ID
        Optional<Banco> bancoOptional = bancoRepository.findById(huespedRecord.idBanco());
        if (bancoOptional.isEmpty()) {
            throw new IllegalArgumentException("Banco no encontrado con ID: " + huespedRecord.idBanco());
        }

        Banco banco = bancoOptional.get();

        // Crear y guardar el usuario
        Huesped usuario = huespedRecord.toHuesped();
        usuarioRepository.save(usuario);

        // Crear y guardar la tarjeta de crédito
        TarjetaCredito tarjetaCredito = huespedRecord.toTarjetaCredito();
        tarjetaCredito.setHuesped(usuario);
        tarjetaCredito.setBanco(banco);
        TarjetaCredito tarjetaCreditoSaved =tarjetaCreditoRepository.save(tarjetaCredito);
        if (usuario.getTarjetaCredito() == null) {
            usuario.setTarjetaCredito(new ArrayList<>());
        }
        usuario.getTarjetaCredito().add(tarjetaCreditoSaved);
        return usuario;
    }

    public void crearUsuarioPropietario(PropietarioRecord propietarioRecord) {
        // Buscar el banco por ID
        Optional<Banco> bancoOptional = bancoRepository.findById(propietarioRecord.cuentaBancaria().idBanco());
        if (bancoOptional.isEmpty()) {
            throw new IllegalArgumentException("Banco no encontrado con ID: " + propietarioRecord.cuentaBancaria().idBanco());
        }

        Banco banco = bancoOptional.get();

        Propietario propietario = propietarioRecord.toPropietario();
        CuentaBancaria cuentaBancaria = propietarioRecord.cuentaBancaria().toCuentaBancaria();
        cuentaBancaria.setBanco(banco);
        propietario.setCuentaBancaria(cuentaBancariaRepository.save(cuentaBancaria));
        usuarioRepository.save(propietario);
    }
    
    public Page<Usuario> buscarPorNombre(String nombre, Pageable pageable) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    public Page<Usuario> buscarPorDni(String dni, Pageable pageable) {
        return usuarioRepository.findByDniContaining(dni, pageable);
    }

    public Usuario buscarPorDniExacto(String dni) {
        return usuarioRepository.findByDni(dni);
    }
}