package edu.utn.frsf.isi.dan.user.service;

import edu.utn.frsf.isi.dan.user.dao.BancoRepository;
import edu.utn.frsf.isi.dan.user.dao.CuentaBancariaRepository;
import edu.utn.frsf.isi.dan.user.dao.TarjetaCreditoRepository;
import edu.utn.frsf.isi.dan.user.dao.UsuarioRepository;
import edu.utn.frsf.isi.dan.user.dto.CuentaBancariaRecord;
import edu.utn.frsf.isi.dan.user.dto.HuespedRecord;
import edu.utn.frsf.isi.dan.user.dto.PropietarioRecord;
import edu.utn.frsf.isi.dan.user.model.Banco;
import edu.utn.frsf.isi.dan.user.model.CuentaBancaria;
import edu.utn.frsf.isi.dan.user.model.Huesped;
import edu.utn.frsf.isi.dan.user.model.Propietario;
import edu.utn.frsf.isi.dan.user.model.TarjetaCredito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private BancoRepository bancoRepository;

    @Mock
    private CuentaBancariaRepository cuentaBancariaRepository;

    @Mock
    private TarjetaCreditoRepository tarjetaCreditoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCrearUsuarioHuesped() {
        // Arrange
        HuespedRecord huespedRecord = mock(HuespedRecord.class);
        Banco banco = mock(Banco.class);
        TarjetaCredito tarjetaCredito = mock(TarjetaCredito.class);
        Huesped huesped = mock(Huesped.class);

        when(huespedRecord.idBanco()).thenReturn(1);
        when(bancoRepository.findById(1)).thenReturn(Optional.of(banco));
        when(huespedRecord.toHuesped()).thenReturn(huesped);
        when(huespedRecord.toTarjetaCredito()).thenReturn(tarjetaCredito);
        when(tarjetaCreditoRepository.save(any(TarjetaCredito.class))).thenReturn(tarjetaCredito);

        // Act
        Huesped result = userService.crearUsuarioHuesped(huespedRecord);

        // Assert
        assertNotNull(result);
        verify(usuarioRepository).save(huesped);
        verify(tarjetaCreditoRepository).save(tarjetaCredito);
        verify(bancoRepository).findById(1);
    }

    @Test
    public void testCrearUsuarioPropietario() {
        // Arrange
        PropietarioRecord propietarioRecord = mock(PropietarioRecord.class);
        CuentaBancariaRecord cuentaRecord = mock(CuentaBancariaRecord.class);
        Banco banco = mock(Banco.class);
        Propietario propietario = mock(Propietario.class);
        CuentaBancaria cuentaBancaria = mock(CuentaBancaria.class);

        when(cuentaRecord.idBanco()).thenReturn(1);
        when(propietarioRecord.cuentaBancaria()).thenReturn(cuentaRecord);
        when(bancoRepository.findById(1)).thenReturn(Optional.of(banco));
        when(propietarioRecord.toPropietario()).thenReturn(propietario);
        when(propietarioRecord.cuentaBancaria().toCuentaBancaria()).thenReturn(cuentaBancaria);
        when(cuentaBancariaRepository.save(cuentaBancaria)).thenReturn(cuentaBancaria);

        // Act
        userService.crearUsuarioPropietario(propietarioRecord);

        // Assert
        verify(usuarioRepository).save(propietario);
        verify(cuentaBancariaRepository).save(cuentaBancaria);
        verify(bancoRepository).findById(1);
    }
}