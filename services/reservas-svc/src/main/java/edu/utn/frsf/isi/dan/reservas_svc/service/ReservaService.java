package edu.utn.frsf.isi.dan.reservas_svc.service;

import edu.utn.frsf.isi.dan.reservas_svc.client.UserServiceClient;
import edu.utn.frsf.isi.dan.reservas_svc.dto.UserDto;
import edu.utn.frsf.isi.dan.reservas_svc.model.Reserva;
import edu.utn.frsf.isi.dan.reservas_svc.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {
    @Autowired
    private ReservaRepository reservaRepository;
    
    @Autowired
    private UserServiceClient userServiceClient;

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> findById(String id) {
        return reservaRepository.findById(id);
    }

    public Reserva save(Reserva reserva) {
        // Validate reservation before saving
        validateReservation(reserva);
        return reservaRepository.save(reserva);
    }
    
    private void validateReservation(Reserva reserva) {
        // Validate user exists using idUsuario from Huesped
        if (reserva.getHuesped() != null && reserva.getHuesped().getIdUsuario() != null) {
            try {
                Long userId = Long.parseLong(reserva.getHuesped().getIdUsuario());
                UserDto user = userServiceClient.getHuesped(userId);
                if (user == null) {
                    throw new RuntimeException("Huésped no encontrado con ID: " + reserva.getHuesped().getIdUsuario());
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("ID de huésped inválido: " + reserva.getHuesped().getIdUsuario());
            } catch (Exception e) {
                throw new RuntimeException("Error validando huésped: " + e.getMessage());
            }
        }
    }

    public void deleteById(String id) {
        reservaRepository.deleteById(id);
    }
}
