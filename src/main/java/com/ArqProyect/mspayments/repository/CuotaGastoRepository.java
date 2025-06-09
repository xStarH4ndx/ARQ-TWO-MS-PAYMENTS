package com.ArqProyect.mspayments.repository;

import com.ArqProyect.mspayments.model.CuotaGasto;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CuotaGastoRepository extends MongoRepository<CuotaGasto, String> {
    List<CuotaGasto> findByUserId(String userId);
    List<CuotaGasto> findByGastoId(String gastoId);
    List<CuotaGasto> findByEstadoPago(boolean estadoPago);
}
