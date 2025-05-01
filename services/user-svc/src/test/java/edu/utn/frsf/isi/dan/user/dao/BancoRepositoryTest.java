package edu.utn.frsf.isi.dan.user.dao;

import edu.utn.frsf.isi.dan.user.model.Banco;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
public class BancoRepositoryTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static {
        mysqlContainer.start();
        System.setProperty("DB_URL", mysqlContainer.getJdbcUrl());
        System.setProperty("DB_USERNAME", mysqlContainer.getUsername());
        System.setProperty("DB_PASSWORD", mysqlContainer.getPassword());
    }

    @Autowired
    private BancoRepository bancoRepository;

    @Test
    public void testCreateBanco() {
        Banco banco = Banco.builder().nombre("Banco Test").build();
        Banco savedBanco = bancoRepository.save(banco);
        assertNotNull(savedBanco.getId());
        assertEquals("Banco Test", savedBanco.getNombre());
    }

    @Test
    public void testDeleteBanco() {
        Banco banco = Banco.builder().nombre("Banco Test").build();
        Banco savedBanco = bancoRepository.save(banco);
        bancoRepository.deleteById(savedBanco.getId());
        Optional<Banco> deletedBanco = bancoRepository.findById(savedBanco.getId());
        assertTrue(deletedBanco.isEmpty());
    }

    @Test
    public void testUpdateBanco() {
        Banco banco = Banco.builder().nombre("Banco Test").build();
        Banco savedBanco = bancoRepository.save(banco);
        savedBanco.setNombre("Banco Actualizado");
        Banco updatedBanco = bancoRepository.save(savedBanco);
        assertEquals("Banco Actualizado", updatedBanco.getNombre());
    }

    @Test
    public void testFindBancoById() {
        Banco banco = Banco.builder().nombre("Banco Test").build();
        Banco savedBanco = bancoRepository.save(banco);
        Optional<Banco> foundBanco = bancoRepository.findById(savedBanco.getId());
        assertTrue(foundBanco.isPresent());
        assertEquals("Banco Test", foundBanco.get().getNombre());
    }

    @Test
    public void testFindBancoByNombre() {
        Banco banco = Banco.builder().nombre("Banco Test").build();
        bancoRepository.save(banco);
        Banco foundBanco = bancoRepository.findAll().stream()
                .filter(b -> b.getNombre().equals("Banco Test"))
                .findFirst()
                .orElse(null);
        assertNotNull(foundBanco);
        assertEquals("Banco Test", foundBanco.getNombre());
    }
}