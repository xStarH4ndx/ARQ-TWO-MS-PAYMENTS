package com.ArqProyect.mspayments.dto;

import java.util.List;

import com.ArqProyect.mspayments.model.ItemCompraPlayload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraPlayloadDTO {
    @NotBlank(message = "El ID de la Compra es obligatorio")
    private String compraId;

    @NotBlank(message = "El ID de la casa es obligatorio")
    private String casaId;

    @NotBlank(message = "La fecha de compra es obligatoria")
    private String fechaCompra; // Usar String para manejar formato ISO 8601

    @NotBlank(message = "Los items de compra son obligatorios")
    private List<ItemCompraPlayload> itemsCompra; // Usar String para manejar JSON de items de compra
}
