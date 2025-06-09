package com.ArqProyect.mspayments.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GastoServicioDTO {

    @NotBlank(message = "El ID de la casa es obligatorio")
    private String casaId;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El valor total es obligatorio")
    @Min(value = 0, message = "El valor total debe ser mayor o igual a 0")
    private Double valorTotal;

    @NotBlank(message = "La fecha de renovación es obligatoria")
    private LocalDate fechaRenovacion;
}
