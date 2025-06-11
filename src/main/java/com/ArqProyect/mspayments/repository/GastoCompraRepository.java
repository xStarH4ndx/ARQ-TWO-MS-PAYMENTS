package com.ArqProyect.mspayments.repository;

import com.ArqProyect.mspayments.model.GastoCompra;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface GastoCompraRepository extends MongoRepository<GastoCompra, String> {
    List<GastoCompra> findByCasaId(String casaId);
    GastoCompra findByCompraId(String compraId);
    GastoCompra findTopByCasaIdOrderByFechaRegistroDesc(String casaId); 
}
