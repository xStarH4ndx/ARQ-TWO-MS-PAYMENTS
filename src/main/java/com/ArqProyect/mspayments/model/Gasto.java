package com.ArqProyect.mspayments.model;

import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Gasto{
    @Id
    private String id;
    private String casaId;
    private String descripcion;
    private String fechaRegistro;
}
