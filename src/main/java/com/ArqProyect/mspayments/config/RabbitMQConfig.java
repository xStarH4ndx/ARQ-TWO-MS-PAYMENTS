package com.ArqProyect.mspayments.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Definicion de la cola de MSPayments escucha a MSInventory
    @Bean
    public Queue pagosComprasQueue() {
        return new Queue("pagos.compras.queue", true);
    }

    // Definicion de la cola de MSPayments escucha a MSInventory
    // se enlaza a un DirectExchange (especifico)
    @Bean
    public DirectExchange inventoryExchange() {
        return new DirectExchange("inventory.exchange");
    }

    // Definicion del Binding que enlaza la colsa "pagosComprasQueue" con el exchange "inventoryExchange"
    // con el routing key "pagos.compras"
    @Bean
    public Binding compraBindingDirect(Queue pagosComprasQueue, DirectExchange invDirectExchange) {
        return BindingBuilder.bind(pagosComprasQueue).to(invDirectExchange).with("pagos.compras");
    }


}
