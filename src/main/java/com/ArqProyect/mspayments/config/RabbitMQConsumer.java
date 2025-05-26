package com.ArqProyect.mspayments.config;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RabbitMQConsumer {
    
    // @Autowired
    // private PagoService pagoService;

    @Autowired
    private ObjectMapper ObjectMapper;

    @RabbitListener(queues = "pagos.compras.queue")
    public void recibirMensajeCompra(String mensaje) {
        System.out.println("Mensaje recibido en pagos.compras.queue: " + mensaje);
        // try {
            
        // }
    }
}
