package com.ArqProyect.mspayments.config;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ArqProyect.mspayments.dto.CompraPlayloadDTO;
import com.ArqProyect.mspayments.dto.GastoCompraDTO;
import com.ArqProyect.mspayments.dto.ItemCompraDTO;
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
                case "eliminarGastoCompra":
                    return handleEliminarGastoCompra(data);
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
    private String handleEliminarGastoCompra(JsonNode data) {
        try {
            if (data == null || !data.hasNonNull("compraId")) {
                throw new IllegalArgumentException("El campo 'compraId' es requerido y no puede ser nulo");
            }
            String compraId = data.get("compraId").asText();
            gastoCompraService.eliminarGastoCompra(compraId);
            String msg = "GastoCompra eliminado exitosamente: " + compraId;
            System.out.println(msg);
            return msg;
        } catch (Exception e) {
            String msg = "Error al eliminar GastoCompra: " + e.getMessage();
            System.err.println(msg);
            return msg;
        }
    }

    private String handleCrearGastoCompra(JsonNode data) {
        try {
            if (data == null) {
                throw new IllegalArgumentException("El cuerpo del mensaje no puede ser nulo");
            }
            CompraPlayloadDTO dto = objectMapper.treeToValue(data, CompraPlayloadDTO.class);
            List<ItemCompraDTO> itemDTOs = dto.getItemsCompra().stream().map(item -> {
                ItemCompraDTO itemDTO = new ItemCompraDTO();
                itemDTO.setProductoId(item.getProductoId());
                itemDTO.setNombreProducto(item.getNombreProducto());
                itemDTO.setCantidad(item.getCantidad());
                itemDTO.setPrecioUnitario(item.getPrecioUnitario());
                itemDTO.setEsCompartido(item.getEsCompartido());
                itemDTO.setPropietarioId(item.getPropietarioId());
                return itemDTO;
            }).toList();

            //Calcular totales
            double totalIndividual = itemDTOs.stream()
                    .filter(item -> !Boolean.TRUE.equals(item.getEsCompartido()))
                    .mapToDouble(item -> item.getPrecioUnitario() * item.getCantidad())
                    .sum();

            double totalCompartido = itemDTOs.stream()
                    .filter(item -> Boolean.TRUE.equals(item.getEsCompartido()))
                    .mapToDouble(item -> item.getPrecioUnitario() * item.getCantidad())
                    .sum();

            GastoCompraDTO gasto = new GastoCompraDTO();
            gasto.setCompraId(dto.getCompraId());
            gasto.setCasaId(dto.getCasaId());
            gasto.setDescripcion("Compra de Insumos");
            gasto.setItemsCompra(itemDTOs);
            gasto.setValorTotalIndividual(totalIndividual);
            gasto.setValorTotalCompartido(totalCompartido);

            var gastoGuardado = gastoCompraService.crearGastoCompraDesdeDTO(gasto);
            String msg= "GastoCompra creado exitosamente: " + gastoGuardado.getId();
            System.out.println(msg);
            return msg;
        } catch (Exception e) {
            String msg = "Error al procesar GastoCompra: " + e.getMessage();
            return msg;
        }
    }

    // GASTO COMPRA - APIGATEWAY
    private List<GastoCompra> handleListarGastoComprasCasa(JsonNode data) {
        if(data == null || !data.isTextual()) {
            throw new IllegalArgumentException("El campo 'casaId' debe ser un texto no nulo");
        }
        String casaId = data.asText();
        return gastoCompraService.getGastosByCasa(casaId);
    }

}
