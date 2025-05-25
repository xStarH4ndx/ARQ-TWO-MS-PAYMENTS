package com.ArqProyect.mspayments.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "gastoServicio")
public class GastoServicio extends Gasto{
    private double valorTotal;
    private String fechaRenovacion;
}
