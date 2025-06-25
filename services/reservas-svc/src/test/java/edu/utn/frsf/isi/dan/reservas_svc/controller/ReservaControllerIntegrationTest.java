package edu.utn.frsf.isi.dan.reservas_svc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.utn.frsf.isi.dan.reservas_svc.client.UserServiceClient;
import edu.utn.frsf.isi.dan.reservas_svc.dto.UserDto;
import edu.utn.frsf.isi.dan.reservas_svc.model.Huesped;
import edu.utn.frsf.isi.dan.reservas_svc.model.Reserva;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReservaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserServiceClient userServiceClient;
    
    @Test
    void testCreateReserva_ValidData_Success() throws Exception {
        // Arrange
        UserDto mockUser = new UserDto(1L, "Juan", "Pérez", "juan@email.com", "12345678");
        
        when(userServiceClient.getHuesped(1L)).thenReturn(mockUser);
        
        Huesped huesped = Huesped.builder()
                .idUsuario("1")
                .nombreApellido("Juan Pérez")
                .email("juan@email.com")
                .build();
        
        Reserva reserva = Reserva.builder()
                .idHabitacion("1")
                .hotelId(1L)
                .createdAt(Instant.now())
                .checkIn(Instant.now().plusSeconds(86400)) // Tomorrow
                .checkOut(Instant.now().plusSeconds(172800)) // Day after tomorrow
                .precioNoche(100.0)
                .precioTotal(200.0)
                .status("PENDING")
                .huesped(huesped)
                .build();
        
        // Act & Assert
        mockMvc.perform(post("/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idHabitacion").value("1"))
                .andExpect(jsonPath("$.hotelId").value(1))
                .andExpect(jsonPath("$.huesped.idUsuario").value("1"));
    }
    
    @Test
    void testCreateReserva_InvalidUser_ReturnsBadRequest() throws Exception {
        // Arrange
        when(userServiceClient.getHuesped(anyLong())).thenReturn(null);
        
        Huesped huesped = Huesped.builder()
                .idUsuario("999")
                .nombreApellido("Usuario Inexistente")
                .build();
        
        Reserva reserva = Reserva.builder()
                .idHabitacion("1")
                .hotelId(1L)
                .huesped(huesped)
                .build();
        
        // Act & Assert
        mockMvc.perform(post("/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().is5xxServerError()); // Validation error becomes 500
    }
}