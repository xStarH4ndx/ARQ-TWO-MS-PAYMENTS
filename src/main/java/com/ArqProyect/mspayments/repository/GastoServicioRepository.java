package com.ArqProyect.mspayments.repository;

import com.ArqProyect.mspayments.model.GastoServicio;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface GastoServicioRepository extends MongoRepository<GastoServicio, String> {
    List<GastoServicio> findByCasaId(String casaId);
}
