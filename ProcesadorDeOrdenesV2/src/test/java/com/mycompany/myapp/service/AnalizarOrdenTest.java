package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.repository.OrdenRepository;
import com.tngtech.archunit.core.domain.Dependency;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


class AnalizarOrdenTest {

//    @Test
//    void analizarOrdenes() throws Exception{
//
//        CleanerService mockedCleanerService = mock(CleanerService.class);
//        OrdenRepository mockedOrdenRepository = mock(OrdenRepository.class);
//        DataVerificationService mockedDataVerificationService = mock(DataVerificationService.class);
//        CompraVentaService mockedCompraVentaService = mock(CompraVentaService.class);
//        ReporteOperacionesService mockedReporteOperacionesService = mock(ReporteOperacionesService.class);
//        LoggerService mockedLoggerService = mock(LoggerService.class);
//        ProgramarOperacionService mockedProgramarOperacionService = mock(ProgramarOperacionService.class);
//
//        List<Orden> listaOrdenes = new ArrayList<>();
//
//        Orden orden1 = new Orden()
//            .cliente(201225)
//            .accionId(1)
//            .accion("AAPL")
//            .operacion("COMPRA")
//            .precio(12F)
//            .cantidad(5)
//            .fechaOperacion(LocalDateTime.now())
//            .modo("AHORA");
//
//        Orden orden2 = new Orden()
//            .cliente(201225)
//            .accionId(1)
//            .accion("AAPL")
//            .operacion("VENTA")
//            .precio(12F)
//            .cantidad(5)
//            .fechaOperacion(LocalDateTime.now())
//            .modo("AHORA");
//
//        listaOrdenes.add(orden1);
//        listaOrdenes.add(orden2);
//
//        AnalizarOrdenService analizarOrdenService = new AnalizarOrdenService(mockedCleanerService, mockedOrdenRepository, mockedDataVerificationService, mockedCompraVentaService, mockedReporteOperacionesService, mockedLoggerService, mockedProgramarOperacionService);
//
//        assertTrue(analizarOrdenService.analizarOrdenes(Mono.just(listaOrdenes)));
//    }
}
