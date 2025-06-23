package com.ArqProyect.mspayments.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ArqProyect.mspayments.dto.CuotaGastoDTO;
import com.ArqProyect.mspayments.dto.GastoCompraDTO;
import com.ArqProyect.mspayments.dto.GastoServicioDTO;
import com.ArqProyect.mspayments.dto.ItemCompraDTO;
import com.ArqProyect.mspayments.model.CuotaGasto;
import com.ArqProyect.mspayments.model.GastoCompra;
import com.ArqProyect.mspayments.model.GastoServicio;
import com.ArqProyect.mspayments.repository.CuotaGastoRepository;
import com.ArqProyect.mspayments.repository.GastoCompraRepository;
import com.ArqProyect.mspayments.repository.GastoServicioRepository;
import com.ArqProyect.mspayments.services.CuotaGastoService;
import com.ArqProyect.mspayments.services.GastoCompraService;
import com.ArqProyect.mspayments.services.GastoServicioService;

class ServicesTest {

    @Mock
    private CuotaGastoRepository cuotaGastoRepository;

    @Mock
    private GastoCompraRepository gastoCompraRepository;

    @Mock
    private GastoServicioRepository gastoServicioRepository;

    @InjectMocks
    private CuotaGastoService cuotaGastoService;

    @InjectMocks
    private GastoCompraService gastoCompraService;

    @InjectMocks
    private GastoServicioService gastoServicioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // CuotaGastoService tests
    @Test
    void testCrearCuotaDesdeDTO() {
        CuotaGastoDTO dto = new CuotaGastoDTO("gasto1", "user1", 100.0, false);
        CuotaGasto cuota = new CuotaGasto(null, "gasto1", "user1", 100.0, false);
        when(cuotaGastoRepository.save(any(CuotaGasto.class))).thenReturn(cuota);

        CuotaGasto result = cuotaGastoService.crearCuotaDesdeDTO(dto);

        assertEquals("gasto1", result.getGastoId());
        assertEquals("user1", result.getUserId());
        assertEquals(100.0, result.getValorCuota());
        assertFalse(result.isEstadoPago());
    }

    @Test
    void testGetCuotasByUser() {
        CuotaGasto cuota = new CuotaGasto("1", "gasto1", "user1", 100.0, false);
        when(cuotaGastoRepository.findByUserId("user1")).thenReturn(List.of(cuota));

        List<CuotaGasto> result = cuotaGastoService.getCuotasByUser("user1");

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUserId());
    }

    @Test
    void testMarcarComoPagada() {
        CuotaGasto cuota = new CuotaGasto("1", "gasto1", "user1", 100.0, false);
        when(cuotaGastoRepository.findById("1")).thenReturn(Optional.of(cuota));

        cuotaGastoService.marcarComoPagada("1");

        assertTrue(cuota.isEstadoPago());
        verify(cuotaGastoRepository).save(cuota);
    }

    @Test
    void testEliminarCuotasPorGastoId() {
        CuotaGasto cuota1 = new CuotaGasto("1", "gasto1", "user1", 50.0, false);
        CuotaGasto cuota2 = new CuotaGasto("2", "gasto1", "user2", 75.0, false);
        when(cuotaGastoRepository.findByGastoId("gasto1")).thenReturn(Arrays.asList(cuota1, cuota2));

        cuotaGastoService.eliminarCuotasPorGastoId("gasto1");

        verify(cuotaGastoRepository).deleteAll(Arrays.asList(cuota1, cuota2));
    }

    @Test
    void testEliminarCuotasPorGastoId_NoEncontradas() {
        when(cuotaGastoRepository.findByGastoId("gastoX")).thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cuotaGastoService.eliminarCuotasPorGastoId("gastoX");
        });

        assertEquals("No se encontraron cuotas para el gastoId: gastoX", exception.getMessage());
    }

    // GastoCompraService tests
    @Test
    void testCrearGastoCompraDesdeDTO() {
        ItemCompraDTO itemDTO = new ItemCompraDTO("prod1", "Producto", 2, 10.0, false, "user1");
        GastoCompraDTO dto = new GastoCompraDTO("casa1", "compra semanal", "compra1", List.of(itemDTO), 100.0, 50.0);

        when(gastoCompraRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        GastoCompra result = gastoCompraService.crearGastoCompraDesdeDTO(dto);

        assertEquals("casa1", result.getCasaId());
        assertEquals("compra1", result.getCompraId());
        assertEquals(1, result.getItemsCompra().size());
    }

    @Test
    void testEliminarGastoCompra() {
        GastoCompra gasto = new GastoCompra();
        gasto.setCompraId("compra1");

        when(gastoCompraRepository.findByCompraId("compra1")).thenReturn(gasto);

        gastoCompraService.eliminarGastoCompra("compra1");

        verify(gastoCompraRepository).delete(gasto);
    }

    @Test
    void testEliminarGastoCompra_NotFound() {
        when(gastoCompraRepository.findByCompraId("compraX")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gastoCompraService.eliminarGastoCompra("compraX");
        });

        assertEquals("GastoCompra no encontrado con ID: compraX", exception.getMessage());
    }

    // GastoServicioService tests
    @Test
    void testCrearGastoServicioDesdeDTO() {
        GastoServicioDTO dto = new GastoServicioDTO("casa1", "servicio de internet", 50.0, "2025-01-01");

        when(gastoServicioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        GastoServicio result = gastoServicioService.crearGastoServicioDesdeDTO(dto);

        assertEquals("casa1", result.getCasaId());
        assertEquals("servicio de internet", result.getDescripcion());
        assertEquals(50.0, result.getValorTotal());
        assertEquals("2025-01-01", result.getFechaRenovacion());
    }

    @Test
    void testGetGastosByCasa_Servicio() {
        GastoServicio gasto = new GastoServicio();
        gasto.setCasaId("casa1");
        when(gastoServicioRepository.findByCasaId("casa1")).thenReturn(List.of(gasto));

        List<GastoServicio> result = gastoServicioService.getGastosByCasa("casa1");

        assertEquals(1, result.size());
        assertEquals("casa1", result.get(0).getCasaId());
    }
}
