package com.ArqProyect.mspayments.config;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {



    @RabbitListener(queues = "mspayments.queue")
    public Object handlePaymentQueue(MessageDTO messageDTO){
        try{
            PayloadDTO payload = messageDTO.getData();
            if(payload == null) {
                return "Error: 'data' no encontrado en mensaje";
            }

            String action = payload.getAction();
            JsonNode data = payload.getBody();

            switch (action){
                case "crearGastoCompra":
                    System.out.println("Accion: crearGastoCompra");
                    return handleCrearGastoCompra(data);
                default:
                    System.out.println("MS-PAYMENT: Accion no reconocida: " + action);
                    return "Accion no reconocida: " + action;
            }
        } catch (Exception e) {
            System.err.println("Error procesando mensaje: " + e.getMessage());
            e.printStackTrace();
            return "Error procesando mensaje: " + e.getMessage();
        }
    }

    private Object handleCrearGastoCompra(JsonNode data) {
        // Implementar la logica para manejar la creacion de GastoCompra
        System.out.println("Procesando GastoCompra: " + data);
        // Aqui se puede agregar la logica para guardar el gasto en la base de datos
        return null;
    }
}
