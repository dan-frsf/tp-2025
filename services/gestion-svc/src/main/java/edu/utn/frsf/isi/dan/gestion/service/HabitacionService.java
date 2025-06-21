package edu.utn.frsf.isi.dan.gestion.service;

import edu.utn.frsf.isi.dan.gestion.dao.HabitacionRepository;
import edu.utn.frsf.isi.dan.gestion.model.Habitacion;
import edu.utn.frsf.isi.dan.shared.HabitacionDTO;
import edu.utn.frsf.isi.dan.shared.HabitacionEvent;
import edu.utn.frsf.isi.dan.shared.TipoEvento;
import lombok.extern.log4j.Log4j2;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
public class HabitacionService {

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange:habitacion.exchange}")
    private String exchange;
    @Value("${rabbitmq.routingkey:habitacion.key}")
    private String routingKey;

    public Habitacion save(Habitacion habitacion) {
        log.info("Habltaicon {} ", habitacion);
        boolean isNew = Objects.isNull(habitacion.getId());
        Habitacion newHabitacion = habitacionRepository.save(habitacion);
        enviarHabitacionJms(newHabitacion, isNew);
        return newHabitacion;
    }

    public void deleteById(Integer id) {
        enviarHabitacionJms(id);
        habitacionRepository.deleteById(id);
    }

    public Optional<Habitacion> findById(Integer id) {
        return habitacionRepository.findById(id);
    }

    public List<Habitacion> findAll() {
        return habitacionRepository.findAll();
    }

    public void enviarHabitacionJms(Habitacion habitacion,boolean isNew) {
        HabitacionDTO dto = HabitacionDTO.builder()
                .habitacionId(habitacion.getId().longValue())
                .numero(habitacion.getNumero())
                .tipoHabitacionId(habitacion.getTipoHabitacion().getId())
                .tipoHabitacion(habitacion.getTipoHabitacion().getDescripcion())
                .capacidad(habitacion.getTipoHabitacion().getCapacidad())
                .precioNoche(Double.valueOf(0.0))
                .hotel(null)
                .build();
        // agregar la tarifa que le corresponde por el tipo de habitacion
        // agregar el hotel        
        HabitacionEvent msgEvent = HabitacionEvent.builder()
                .tipoEvento(isNew? TipoEvento.CREAR : TipoEvento.ACTUALIZAR_DATOS)
                .habitacion(dto)
                .build();
        try {
            String msgToSend = objectMapper.writeValueAsString(msgEvent);
            log.debug("[RabbitMQ] Enviando mensaje: {}", msgToSend);    
            rabbitTemplate.convertAndSend(exchange, routingKey, msgToSend);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enviarHabitacionJms(Integer id) {  
        HabitacionDTO dto = HabitacionDTO.builder()
                .habitacionId(id.longValue()).build();      
        HabitacionEvent msgEvent = HabitacionEvent.builder()
                .tipoEvento(TipoEvento.ELIMINAR)
                .habitacion(dto)
                .build();
        try {
            String msgToSend = objectMapper.writeValueAsString(msgEvent);
            log.debug("[RabbitMQ] Enviando mensaje: {}", msgToSend);     
            rabbitTemplate.convertAndSend(exchange, routingKey, msgToSend);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
