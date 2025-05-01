package edu.utn.frsf.isi.dan.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.utn.frsf.isi.dan.user.dto.CuentaBancariaRecord;
import edu.utn.frsf.isi.dan.user.dto.HuespedRecord;
import edu.utn.frsf.isi.dan.user.dto.PropietarioRecord;
import edu.utn.frsf.isi.dan.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCrearUsuarioHuesped() throws Exception {
        // Arrange
        HuespedRecord huespedRecord = new HuespedRecord(
            "Jane", 
            "jane.smith@example.com", 
            "9876543210", 
            LocalDate.of(1990, 5, 15), 
            "1234567890123456", 
            "12/25", 
            "123", 
            true, 
            1
        );

        // Act & Assert
        mockMvc.perform(post("/users/huesped")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(huespedRecord)))
            .andExpect(status().isCreated());
    }

    @Test
    public void testCrearUsuarioPropietario() throws Exception {
        // Arrange
        // Assuming CuentaBancariaRecord is a required field for PropietarioRecord
        CuentaBancariaRecord cuentaBancariaRecord = new CuentaBancariaRecord("123456789", "BankName", "BranchName",1);
        PropietarioRecord propietarioRecord = new PropietarioRecord("John", "Doe", "john.doe@example.com", 1234567890L, cuentaBancariaRecord);

        // Act & Assert
        mockMvc.perform(post("/users/propietario")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(propietarioRecord)))
                .andExpect(status().isCreated());
    }
}