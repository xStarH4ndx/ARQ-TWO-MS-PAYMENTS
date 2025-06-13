package com.ArqProyect.mspayments.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ArqProyect.mspayments.dto.GastoCompraDTO;
import com.ArqProyect.mspayments.model.GastoCompra;
import com.ArqProyect.mspayments.model.ItemCompraPlayload;
import com.ArqProyect.mspayments.repository.GastoCompraRepository;

@Service
public class GastoCompraService {

    @Autowired
    private GastoCompraRepository gastoCompraRepository;

    public void eliminarGastoCompra(String compraId) {
        GastoCompra gasto = gastoCompraRepository.findByCompraId(compraId);
        if (gasto == null) {
            throw new RuntimeException("GastoCompra no encontrado con ID: " + compraId);
        }
        gastoCompraRepository.delete(gasto);
    }

    public GastoCompra crearGastoCompraDesdeDTO(GastoCompraDTO dto) {
        GastoCompra gasto = new GastoCompra();
        gasto.setCasaId(dto.getCasaId());
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setFechaRegistro(LocalDateTime.now().toString());
        gasto.setCompraId(dto.getCompraId());
        gasto.setItemsCompra(
            dto.getItemsCompra().stream().map(itemDTO -> {
                ItemCompraPlayload item = new ItemCompraPlayload();
                item.setProductoId(itemDTO.getProductoId());
                item.setNombreProducto(itemDTO.getNombreProducto());
                item.setCantidad(itemDTO.getCantidad());
                item.setPrecioUnitario(itemDTO.getPrecioUnitario());
                item.setEsCompartido(itemDTO.getEsCompartido());
                item.setPropietarioId(itemDTO.getPropietarioId());
                return item;
            }).collect(Collectors.toList())
        );
        gasto.setValorTotalCompartido(dto.getValorTotalCompartido() != null ? dto.getValorTotalCompartido() : 0);
        gasto.setValorTotalIndividual(dto.getValorTotalIndividual() != null ? dto.getValorTotalIndividual() : 0);
        return gastoCompraRepository.save(gasto);
    }

    public List<GastoCompra> getGastosByCasa(String casaId) {
        return gastoCompraRepository.findByCasaId(casaId);
    }

    public GastoCompra getUltimoGastoByCasa(String casaId) {
        GastoCompra gasto = gastoCompraRepository.findTopByCasaIdOrderByFechaRegistroDesc(casaId);
        if (gasto == null) {
            throw new RuntimeException("No se encontró ningún gasto para la casa con ID: " + casaId);
        }
        return gasto;
    }

}

