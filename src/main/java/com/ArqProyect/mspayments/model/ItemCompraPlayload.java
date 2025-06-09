package com.ArqProyect.mspayments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCompraPlayload {
    private String productoId;
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Boolean esCompartido;
    private String propietarioId;
}
