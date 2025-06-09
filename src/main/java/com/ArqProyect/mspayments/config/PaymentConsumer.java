package com.ArqProyect.mspayments.config;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ArqProyect.mspayments.dto.GastoCompraDTO;
import com.ArqProyect.mspayments.model.GastoCompra;
// import com.ArqProyect.mspayments.services.CuotaGastoService;
import com.ArqProyect.mspayments.services.GastoCompraService;
// import com.ArqProyect.mspayments.services.GastoServicioService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final GastoCompraService gastoCompraService;
    // private final GastoServicioService gastoServicioService;
    // private final CuotaGastoService cuotaGastoService;
    private final ObjectMapper objectMapper;

    // escuchar ms-inventory
    @RabbitListener(queues = "gastoCompra.queue")
    public Object handleCuotaGastoQueue(MessageDTO messageDTO){
        try{
            PayloadDTO payload = messageDTO.getData();
            if(payload == null) {
                return "Error: 'data' no encontrado en mensaje";
            }

            String action = payload.getAction();
            JsonNode data = payload.getBody();

            switch (action){
                //  GASTO COMPRA
                case "crearGastoCompra":
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

    // escuchar apigateway
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
                //  GASTO COMPRA
                case "listarGastoCompra":
                    return handleListarGastoComprasCasa(data);

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


    // GASTO COMPRA - MS-INVENTORY
    private Object handleCrearGastoCompra(JsonNode data) {
        try {
            if(data == null) {
                throw new IllegalArgumentException("El cuerpo del mensaje no puede ser nulo");
            }
            GastoCompraDTO dto = objectMapper.treeToValue(data, GastoCompraDTO.class);
            var gastoGuardado = gastoCompraService.crearGastoCompraDesdeDTO(dto);
            System.out.println("GastoCompra creado con ID: " + gastoGuardado.getId());
            return "GastoCompra creado exitosamente";
        } catch (Exception e) {
            System.err.println("Error al procesar GastoCompra: " + e.getMessage());
            e.printStackTrace();
            return "Error al procesar GastoCompra: " + e.getMessage();
        }
    }

    // GASTO COMPRA - APIGATEWAY
    private List<GastoCompra> handleListarGastoComprasCasa(JsonNode data) {
        if(data == null || !data.isTextual()) {
            throw new IllegalArgumentException("El campo 'casaId' debe ser un texto no nulo");
        }
        String cadaId = data.asText();
        return gastoCompraService.getGastosByCasa(cadaId);
    }

}
