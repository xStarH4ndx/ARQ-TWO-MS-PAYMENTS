package com.ArqProyect.mspayments.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraPlayload {
    private String id;
    private String casaId;
    private Instant fechaCompra;
    private List<ItemCompraPlayload> itemsCompra;
}
