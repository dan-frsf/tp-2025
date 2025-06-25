package edu.utn.frsf.isi.dan.reservas_svc.service;

import edu.utn.frsf.isi.dan.reservas_svc.client.UserServiceClient;
import edu.utn.frsf.isi.dan.reservas_svc.dto.UserDto;
import edu.utn.frsf.isi.dan.reservas_svc.model.Huesped;
import edu.utn.frsf.isi.dan.reservas_svc.model.Reserva;
import edu.utn.frsf.isi.dan.reservas_svc.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;
    
    @Mock
    private UserServiceClient userServiceClient;
    
    @InjectMocks
    private ReservaService reservaService;
    
    private Reserva validReserva;
    private UserDto validUser;
    
    @BeforeEach
    void setUp() {
        // Setup valid test data
        validUser = new UserDto(1L, "Juan", "Pérez", "juan@email.com", "12345678");
        
        Huesped huesped = Huesped.builder()
                .idUsuario("1")
                .nombreApellido("Juan Pérez")
                .email("juan@email.com")
                .build();
        
        validReserva = Reserva.builder()
                .idHabitacion("1")
                .hotelId(1L)
                .huesped(huesped)
                .build();
    }
    
    @Test
    void testSave_ValidReservation_Success() {
        // Arrange
        when(userServiceClient.getHuesped(1L)).thenReturn(validUser);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(validReserva);
        
        // Act
        Reserva result = reservaService.save(validReserva);
        
        // Assert
        assertNotNull(result);
        verify(userServiceClient).getHuesped(1L);
        verify(reservaRepository).save(validReserva);
    }
    
    @Test
    void testSave_UserNotFound_ThrowsException() {
        // Arrange
        when(userServiceClient.getHuesped(1L)).thenReturn(null);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> reservaService.save(validReserva));
        
        assertTrue(exception.getMessage().contains("Huésped no encontrado"));
        verify(userServiceClient).getHuesped(1L);
        verify(reservaRepository, never()).save(any());
    }
    
    
    @Test
    void testSave_InvalidUserId_ThrowsException() {
        // Arrange
        Huesped invalidHuesped = Huesped.builder()
                .idUsuario("invalid")
                .build();
        
        Reserva invalidReserva = Reserva.builder()
                .idHabitacion("1")
                .hotelId(1L)
                .huesped(invalidHuesped)
                .build();
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> reservaService.save(invalidReserva));
        
        assertTrue(exception.getMessage().contains("ID de huésped inválido"));
        verify(reservaRepository, never()).save(any());
    }
    
    
    @Test
    void testSave_UserServiceException_ThrowsException() {
        // Arrange
        when(userServiceClient.getHuesped(1L))
                .thenThrow(new RuntimeException("Service unavailable"));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> reservaService.save(validReserva));
        
        assertTrue(exception.getMessage().contains("Error validando huésped"));
        verify(reservaRepository, never()).save(any());
    }
}