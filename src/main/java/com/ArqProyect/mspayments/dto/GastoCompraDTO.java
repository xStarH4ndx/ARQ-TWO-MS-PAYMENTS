package com.ArqProyect.mspayments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GastoCompraDTO {

    @NotBlank(message = "El ID de la casa es obligatorio")
    private String casaId;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "El ID de la compra es obligatorio")
    private String compraId;

    @NotNull(message = "Debe especificar los items de compra")
    private List<ItemCompraDTO> itemsCompra;

    private Double valorTotalCompartido;  // Puede ser null
    private Double valorTotalIndividual;  // Puede ser null
}
