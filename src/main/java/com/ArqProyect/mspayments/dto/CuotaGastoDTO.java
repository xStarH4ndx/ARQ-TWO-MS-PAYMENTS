package com.ArqProyect.mspayments.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuotaGastoDTO {

    @NotBlank(message = "El ID del gasto es obligatorio")
    private String gastoId;

    @NotBlank(message = "El ID del usuario es obligatorio")
    private String userId;

    @NotNull(message = "El valor de la cuota es obligatorio")
    @Min(value = 0, message = "El valor de la cuota no puede ser negativo")
    private Double valorCuota;

    @NotNull(message = "El estado de pago es obligatorio")
    private Boolean estadoPago;
}
