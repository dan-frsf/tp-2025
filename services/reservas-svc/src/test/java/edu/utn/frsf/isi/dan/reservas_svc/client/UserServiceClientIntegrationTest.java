package edu.utn.frsf.isi.dan.reservas_svc.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.utn.frsf.isi.dan.reservas_svc.dto.UserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, 
                classes = {UserServiceClientIntegrationTest.TestConfig.class})
@TestPropertySource(properties = {
    "user-svc.ribbon.listOfServers=localhost:9001"
})
class UserServiceClientIntegrationTest {

    private WireMockServer userServiceMock;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @BeforeEach
    void setUp() {
        userServiceMock = new WireMockServer(9001);
        userServiceMock.start();
    }
    
    @AfterEach
    void tearDown() {
        userServiceMock.stop();
    }
    
    @Test
    void testUserServiceClient_GetHuesped_Success() {
        // Arrange
        userServiceMock.stubFor(get(urlEqualTo("/huespedes/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "id": 1,
                                "nombre": "Juan",
                                "apellido": "Pérez",
                                "email": "juan@email.com",
                                "dni": "12345678"
                            }
                            """)));
        
        // Act
        UserDto result = userServiceClient.getHuesped(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getNombre());
        assertEquals("Pérez", result.getApellido());
        assertEquals("juan@email.com", result.getEmail());
        assertEquals("12345678", result.getDni());
        
        userServiceMock.verify(getRequestedFor(urlEqualTo("/huespedes/1")));
    }
    
    @Test
    void testUserServiceClient_GetUser_Success() {
        // Arrange
        userServiceMock.stubFor(get(urlEqualTo("/usuarios/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "id": 1,
                                "nombre": "Juan",
                                "apellido": "Pérez",
                                "email": "juan@email.com",
                                "dni": "12345678"
                            }
                            """)));
        
        // Act
        UserDto result = userServiceClient.getUser(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getNombre());
        assertEquals("Pérez", result.getApellido());
        
        userServiceMock.verify(getRequestedFor(urlEqualTo("/usuarios/1")));
    }
    
    @Test
    void testUserServiceClient_ServiceUnavailable_ThrowsException() {
        // Arrange
        userServiceMock.stubFor(get(urlEqualTo("/huespedes/1"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));
        
        // Act & Assert
        assertThrows(Exception.class, () -> userServiceClient.getHuesped(1L));
        
        userServiceMock.verify(getRequestedFor(urlEqualTo("/huespedes/1")));
    }
    
    @Test
    void testUserServiceClient_UserNotFound_ReturnsNull() {
        // Arrange
        userServiceMock.stubFor(get(urlEqualTo("/huespedes/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("Not Found")));
        
        // Act & Assert
        assertThrows(Exception.class, () -> userServiceClient.getHuesped(999L));
        
        userServiceMock.verify(getRequestedFor(urlEqualTo("/huespedes/999")));
    }
    
    @Configuration
    @EnableFeignClients(basePackages = "edu.utn.frsf.isi.dan.reservas_svc.client")
    static class TestConfig {
        // Configuration for Feign clients in test context
    }
}