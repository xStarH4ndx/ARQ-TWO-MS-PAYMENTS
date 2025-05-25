package com.ArqProyect.mspayments.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Gasto{
    @Id
    private Long id;
    private String idCasa;
    private String descripcion;
    private LocalDateTime fecha;
}
