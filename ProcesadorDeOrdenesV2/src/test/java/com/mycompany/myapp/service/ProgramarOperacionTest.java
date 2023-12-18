package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Orden;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
class ProgramarOperacionTest {

    @Value("${external.service.url}")
    private String externalServiceUrl;

    @Value("${externalBearer.token}")
    private String bearerToken;


    @Test
    void TestProgramarOperacionFinDia() {

        ReporteOperacionesService reporteOperacionesService = mock(ReporteOperacionesService.class);
        LoggerService loggerService = mock(LoggerService.class);
        CompraVentaService compraVentaService = mock(CompraVentaService.class);
        RestTemplate restTemplate = mock(RestTemplate.class);

        ProgramarOperacionService programarOperacionService = new ProgramarOperacionService(reporteOperacionesService, loggerService, compraVentaService, restTemplate);

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
        RestTemplate restTemplate = mock(RestTemplate.class);

        ProgramarOperacionService programarOperacionService = new ProgramarOperacionService(reporteOperacionesService, loggerService, compraVentaService, restTemplate);

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
        RestTemplate restTemplate = mock(RestTemplate.class);

        ProgramarOperacionService programarOperacionService = new ProgramarOperacionService(reporteOperacionesService, loggerService, compraVentaService, restTemplate);

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

    @Test
    void TestGetCurrentPrice(){

        ReporteOperacionesService reporteOperacionesService = mock(ReporteOperacionesService.class);
        LoggerService loggerService = mock(LoggerService.class);
        CompraVentaService compraVentaService = mock(CompraVentaService.class);
        RestTemplate restTemplate = new RestTemplate();

        ProgramarOperacionService programarOperacionService = new ProgramarOperacionService(reporteOperacionesService, loggerService, compraVentaService, restTemplate);

        Orden orden = new Orden()
            .cliente(12313)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("FINDIA");

//        when(programarOperacionService.getCurrentPrice(orden)).thenReturn("11.0");

        assertThat(programarOperacionService.getCurrentPrice(orden)).isEqualTo("11.0");

    }

}
