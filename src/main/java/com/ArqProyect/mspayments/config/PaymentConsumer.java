package com.ArqProyect.mspayments.config;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.ArqProyect.mspayments.dto.CompraPlayloadDTO;
import com.ArqProyect.mspayments.dto.CuotaGastoDTO;
import com.ArqProyect.mspayments.dto.GastoCompraDTO;
import com.ArqProyect.mspayments.dto.ItemCompraDTO;
import com.ArqProyect.mspayments.model.CasaPlayload;
import com.ArqProyect.mspayments.model.GastoCompra;
import com.ArqProyect.mspayments.services.CuotaGastoService;
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
    private final CuotaGastoService cuotaGastoService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;


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

    // escuchar ms-users
    @RabbitListener(queues = "cuotaPago.queue")
    public void handleCuotaPagoResponse(CasaPlayload casaPayload) {
        try {
            if (casaPayload == null || casaPayload.getUserIds() == null || casaPayload.getUserIds().length == 0) {
                throw new IllegalArgumentException("CasaPlayload o userIds no válidos");
            }

            // Obtener el último GastoCompra (puedes mejorar esto con lógica más robusta)
            var gastoCompra = gastoCompraService.getUltimoGastoByCasa(casaPayload.getId());

            if (gastoCompra == null) {
                throw new RuntimeException("No se encontró GastoCompra para la casa " + casaPayload.getId());
            }

            double totalCompartido = gastoCompra.getValorTotalCompartido();
            double cuotaCompartida = totalCompartido / casaPayload.getUserIds().length;

            // Generar cuotas compartidas
            for (String userId : casaPayload.getUserIds()) {
                CuotaGastoDTO cuotaDTO = new CuotaGastoDTO();
                cuotaDTO.setGastoId(gastoCompra.getId());
                cuotaDTO.setUserId(userId);
                cuotaDTO.setValorCuota(cuotaCompartida);
                cuotaDTO.setEstadoPago(false);
                cuotaGastoService.crearCuotaDesdeDTO(cuotaDTO);
            }

            // Generar cuotas individuales
            gastoCompra.getItemsCompra().stream()
                    .filter(item -> !Boolean.TRUE.equals(item.getEsCompartido()))
                    .forEach(item -> {
                        CuotaGastoDTO cuotaDTO = new CuotaGastoDTO();
                        cuotaDTO.setGastoId(gastoCompra.getId());
                        cuotaDTO.setUserId(item.getPropietarioId());
                        cuotaDTO.setValorCuota(item.getCantidad() * item.getPrecioUnitario());
                        cuotaDTO.setEstadoPago(false);
                        cuotaGastoService.crearCuotaDesdeDTO(cuotaDTO);
                    });

            System.out.println("Cuotas creadas exitosamente para casa: " + casaPayload.getId());
        } catch (Exception e) {
            System.err.println("Error al procesar cuotas: " + e.getMessage());
            e.printStackTrace();
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

            // Calcular totales
            double totalIndividual = itemDTOs.stream()
                    .filter(item -> !Boolean.TRUE.equals(item.getEsCompartido()))
                    .mapToDouble(item -> item.getPrecioUnitario() * item.getCantidad())
                    .sum();

            double totalCompartido = itemDTOs.stream()
                    .filter(item -> Boolean.TRUE.equals(item.getEsCompartido()))
                    .mapToDouble(item -> item.getPrecioUnitario() * item.getCantidad())
                    .sum();

            // Crear GastoCompraDTO
            GastoCompraDTO gasto = new GastoCompraDTO();
            gasto.setCompraId(dto.getCompraId());
            gasto.setCasaId(dto.getCasaId());
            gasto.setDescripcion("Compra de Insumos");
            gasto.setItemsCompra(itemDTOs);
            gasto.setValorTotalIndividual(totalIndividual);
            gasto.setValorTotalCompartido(totalCompartido);

            var gastoGuardado = gastoCompraService.crearGastoCompraDesdeDTO(gasto);

            // ➕ ENVIAR MENSAJE A MS-USERS
            ObjectMapper mapper = new ObjectMapper();
            JsonNode bodyNode = mapper.createObjectNode().put("casaId", dto.getCasaId());

            PayloadDTO payload = new PayloadDTO();
            payload.setAction("getHouseById");
            payload.setBody(bodyNode);

            MessageDTO message = new MessageDTO();
            message.setData(payload);

            rabbitTemplate.convertAndSend("cuotaPago.queue", message);
            System.out.println("Mensaje enviado a ms-users con casaId: " + dto.getCasaId());

            String msg = "GastoCompra creado exitosamente: " + gastoGuardado.getId();
            System.out.println(msg);
            return msg;
        } catch (Exception e) {
            String msg = "Error al procesar GastoCompra: " + e.getMessage();
            System.err.println(msg);
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
