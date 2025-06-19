package edu.utn.frsf.isi.dan.reservas_svc.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

import edu.utn.frsf.isi.dan.reservas_svc.model.Habitacion;
import edu.utn.frsf.isi.dan.reservas_svc.repository.HabitacionRepository;
import edu.utn.frsf.isi.dan.reservas_svc.service.HabitacionService;
import edu.utn.frsf.isi.dan.shared.HabitacionEvent;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;


@Component
@Log4j2
public class GestionMessageListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HabitacionService habitacionService;

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = "habitacion.topic", durable = "true"),
            exchange = @Exchange(value = "dan.exchange", type = "topic"),
            key = "dan.habitacion.#"
        ),
        ackMode = "MANUAL"
    )
    //public void receiveMessage(Message message, com.rabbitmq.client.Channel channel) throws Exception {
    public void receiveMessage(String payload, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag ){   
        try {
            log.debug("[RabbitMQ] Mensaje recibido: {}"+payload);
            HabitacionEvent habitacionEvent = objectMapper.readValue(payload, HabitacionEvent.class);
            log.info("Evento recibido: {}", habitacionEvent);
            // Aquí puedes procesar el evento recibido            
            habitacionService.handleEvent(habitacionEvent);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Error procesando mensaje: {}", e.getMessage());
            // No hago nada: el mensaje NO se reentrega ni se requeuea
            try {
                channel.basicReject(deliveryTag, false);
            } catch (IOException e1) {
                log.error("Error rechazando mensaje: {}", e.getMessage());
            } 
        }
    }
}
