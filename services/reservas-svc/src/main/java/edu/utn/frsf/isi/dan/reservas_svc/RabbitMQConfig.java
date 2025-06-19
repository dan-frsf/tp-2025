package edu.utn.frsf.isi.dan.reservas_svc;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jackson2MessageConverter() {
        // Este convertidor usará Jackson para serializar/deserializar objetos a/desde JSON.
        return new Jackson2JsonMessageConverter();
    }
}
