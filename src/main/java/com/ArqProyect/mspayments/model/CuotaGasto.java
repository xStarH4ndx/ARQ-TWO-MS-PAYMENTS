package com.ArqProyect.mspayments.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "cuotaGasto")
@Data
public class CuotaGasto {
    @Id
    private String id;
    private String idGasto;
}
