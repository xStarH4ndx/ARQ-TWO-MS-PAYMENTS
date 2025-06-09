package com.ArqProyect.mspayments.model;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CasaPlayload {
    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private String codigo;
    private String[] userIds;
}
