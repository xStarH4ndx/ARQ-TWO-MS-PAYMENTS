package com.ArqProyect.mspayments.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "gastoCompra")
public class GastoCompra extends Gasto{
    private String compraId; // ID de la compra asociada
    private List<ItemCompraPlayload> itemsCompra;
    private double valorTotalCompartido; //puede ser null (0)
    private double valorTotalIndividual; //puede ser null (0)
}
