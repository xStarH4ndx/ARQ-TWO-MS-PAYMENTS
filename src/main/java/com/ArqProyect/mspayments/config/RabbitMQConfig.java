package com.ArqProyect.mspayments.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Definicion de la cola de MSPayments escucha a MSInventory
    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable("mspayments.queue").build();
    }

    // Conexion ms-inventory
    @Bean
    public Queue cuotaGastoQueue() {
        return QueueBuilder.durable("gastoCompra.queue").build();
    }

    // Conexion ms-users
    @Bean
    public Queue cuotaPagoQueue() {
        return QueueBuilder.durable("cuotaPago.queue").build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                                MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false); // 👈 evita reintentos infinitos
        return factory;
    }
}
