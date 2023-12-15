package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Orden;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ProgramarOperacionTest {


    @Test
    void TestProgramarOperacionFinDia() {

        ReporteOperacionesService reporteOperacionesService = mock(ReporteOperacionesService.class);
        LoggerService loggerService = mock(LoggerService.class);
        CompraVentaService compraVentaService = mock(CompraVentaService.class);

        ProgramarOperacionService programarOperacionService = new ProgramarOperacionService(reporteOperacionesService, loggerService, compraVentaService);

        Orden ordenFin = new Orden()
            .cliente(12313)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("FINDIA");

        assertThat(programarOperacionService.programarOperacion(ordenFin)).isEqualTo("FINDIA");
    }

    @Test
    void TestProgramarOperacionPrincipioDia(){

        ReporteOperacionesService reporteOperacionesService = mock(ReporteOperacionesService.class);
        LoggerService loggerService = mock(LoggerService.class);
        CompraVentaService compraVentaService = mock(CompraVentaService.class);

        ProgramarOperacionService programarOperacionService = new ProgramarOperacionService(reporteOperacionesService, loggerService, compraVentaService);

        Orden ordenPrincipio = new Orden()
            .cliente(12313)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("PRINCIPIODIA");

        assertThat(programarOperacionService.programarOperacion(ordenPrincipio)).isEqualTo("PRINCIPIODIA");

    }

    @Test
    void TestProgramarOperacionFail(){

        ReporteOperacionesService reporteOperacionesService = mock(ReporteOperacionesService.class);
        LoggerService loggerService = mock(LoggerService.class);
        CompraVentaService compraVentaService = mock(CompraVentaService.class);

        ProgramarOperacionService programarOperacionService = new ProgramarOperacionService(reporteOperacionesService, loggerService, compraVentaService);

        Orden ordenAhora = new Orden()
            .cliente(12313)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");

        assertThat(programarOperacionService.programarOperacion(ordenAhora)).isEqualTo("ERROR");
    }



}
