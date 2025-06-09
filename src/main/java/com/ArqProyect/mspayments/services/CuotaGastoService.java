package com.ArqProyect.mspayments.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ArqProyect.mspayments.dto.CuotaGastoDTO;
import com.ArqProyect.mspayments.model.CuotaGasto;
import com.ArqProyect.mspayments.repository.CuotaGastoRepository;

@Service
public class CuotaGastoService {

    @Autowired
    private CuotaGastoRepository cuotaGastoRepository;

    public CuotaGasto crearCuotaDesdeDTO(CuotaGastoDTO dto) {
        CuotaGasto cuota = new CuotaGasto();
        cuota.setGastoId(dto.getGastoId());
        cuota.setUserId(dto.getUserId());
        cuota.setValorCuota(dto.getValorCuota());
        cuota.setEstadoPago(dto.getEstadoPago());
        return cuotaGastoRepository.save(cuota);
    }

    public List<CuotaGasto> getCuotasByUser(String userId) {
        return cuotaGastoRepository.findByUserId(userId);
    }

    public void marcarComoPagada(String cuotaId) {
        CuotaGasto cuota = cuotaGastoRepository.findById(cuotaId)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));
        cuota.setEstadoPago(true);
        cuotaGastoRepository.save(cuota);
    }
}

