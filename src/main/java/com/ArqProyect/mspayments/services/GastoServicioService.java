package com.ArqProyect.mspayments.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ArqProyect.mspayments.dto.GastoServicioDTO;
import com.ArqProyect.mspayments.model.GastoServicio;
import com.ArqProyect.mspayments.repository.GastoServicioRepository;

@Service
public class GastoServicioService {

    @Autowired
    private GastoServicioRepository gastoServicioRepository;

    public GastoServicio crearGastoServicioDesdeDTO(GastoServicioDTO dto) {
        GastoServicio gasto = new GastoServicio();
        gasto.setCasaId(dto.getCasaId());
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setValorTotal(dto.getValorTotal());
        gasto.setFechaRenovacion(dto.getFechaRenovacion());
        gasto.setFechaRegistro(LocalDateTime.now());
        return gastoServicioRepository.save(gasto);
    }

    public List<GastoServicio> getGastosByCasa(String casaId) {
        return gastoServicioRepository.findByCasaId(casaId);
    }
}

