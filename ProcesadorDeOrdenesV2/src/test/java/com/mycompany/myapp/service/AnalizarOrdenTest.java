package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.repository.OrdenRepository;
import com.tngtech.archunit.core.domain.Dependency;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


class AnalizarOrdenTest {

    DateTimeFormatter dTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    RestTemplate restTemplate = new RestTemplate();
    CleanerService mockedCleanerService = mock(CleanerService.class);
    DataVerificationService dataVerificationService = new DataVerificationService(restTemplate);
    CompraVentaService mockedCompraVentaService = mock(CompraVentaService.class);
    ReporteOperacionesService mockedReporteOperacionesService = mock(ReporteOperacionesService.class);
    LoggerService mockedLoggerService = mock(LoggerService.class);
    ProgramarOperacionService mockedProgramarOperacionService = mock(ProgramarOperacionService.class);
    AnalizarOrdenService analizarOrdenService = new AnalizarOrdenService(mockedCleanerService, dataVerificationService, mockedCompraVentaService, mockedReporteOperacionesService, mockedLoggerService, mockedProgramarOperacionService);

    @Test
    void analyzerTestWrongAccion() throws Exception{

        Orden ordenWrongAccion = new Orden()
            .cliente(201225)
            .accionId(11112312)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");

        int result = analizarOrdenService.analyzer(ordenWrongAccion);

        assertThat(result).isEqualTo(2);
    }

    @Test
    void analyzerTestWrongCliente() throws Exception{

        Orden ordenWrongCliente = new Orden()
            .cliente(123132312)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");

        int result = analizarOrdenService.analyzer(ordenWrongCliente);

        assertThat(result).isEqualTo(3);
    }

    @Test
    void analyzerTestWrongCantidad() throws Exception{

        Orden ordenWrongCantidad = new Orden()
            .cliente(201225)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(0)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");

        int result = analizarOrdenService.analyzer(ordenWrongCantidad);

        assertThat(result).isEqualTo(4);
    }

    @Test
    void analyzerTestComprarAhora() throws Exception{

        Orden ordenComprarAhora = new Orden()
            .cliente(201225)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");

        int result = analizarOrdenService.analyzer(ordenComprarAhora);

        assertThat(result).isEqualTo(5);
    }

    @Test
    void analyzerTestVenderAhora() throws Exception{

        Orden ordenVenderAhora = new Orden()
            .cliente(201225)
            .accionId(1)
            .accion("AAPL")
            .operacion("VENTA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");

        int result = analizarOrdenService.analyzer(ordenVenderAhora);

        assertThat(result).isEqualTo(6);
    }

    @Test
    void analyzerTestProgramarOperacion() throws Exception{

        Orden ordenProgramarOperacion = new Orden()
            .cliente(201225)
            .accionId(1)
            .accion("AAPL")
            .operacion("VENTA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("FINDIA");

        int result = analizarOrdenService.analyzer(ordenProgramarOperacion);

        assertThat(result).isEqualTo(7);
    }

    @Test
    void errorCodeHandlerTestCode2() throws Exception{
        String time = LocalDateTime.now().format(dTF);

        Orden ordenWrongAccion = new Orden()
            .cliente(201225)
            .accionId(11112312)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.parse(time, dTF))
            .modo("AHORA");

        ObjectNode result = analizarOrdenService.errorCodeHandler(2, ordenWrongAccion);

        String expectedResult = "{\"id\":null,\"cliente\":201225,\"accionId\":11112312,\"accion\":\"AAPL\",\"operacion\":\"COMPRA\",\"precio\":12.0,\"cantidad\":5,\"fechaOperacion\":\"" + time + "\",\"modo\":\"AHORA\",\"operacionExitosa\":false,\"operacionObservaciones\":\"La accion no esta disponible.\"}";

        assertThat(result.toString()).isEqualTo(expectedResult);
    }

    @Test
    void errorCodeHandlerTestCode3() throws Exception{
        String time = LocalDateTime.now().format(dTF);

        Orden ordenWrongCliente = new Orden()
            .cliente(123132312)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.parse(time, dTF))
            .modo("AHORA");

        ObjectNode result = analizarOrdenService.errorCodeHandler(3, ordenWrongCliente);

        String expectedResult = "{\"id\":null,\"cliente\":123132312,\"accionId\":1,\"accion\":\"AAPL\",\"operacion\":\"COMPRA\",\"precio\":12.0,\"cantidad\":5,\"fechaOperacion\":\"" + time + "\",\"modo\":\"AHORA\",\"operacionExitosa\":false,\"operacionObservaciones\":\"El cliente no esta disponible.\"}";

        assertThat(result.toString()).isEqualTo(expectedResult);
    }

    @Test
    void errorCodeHandlerTestCode4() throws Exception{
        String time = LocalDateTime.now().format(dTF);

        Orden ordenWrongCantidad = new Orden()
            .cliente(201225)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(0)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");

        ObjectNode result = analizarOrdenService.errorCodeHandler(4, ordenWrongCantidad);

        String expectedResult = "{\"id\":null,\"cliente\":201225,\"accionId\":1,\"accion\":\"AAPL\",\"operacion\":\"COMPRA\",\"precio\":12.0,\"cantidad\":0,\"fechaOperacion\":\"" + time + "\",\"modo\":\"AHORA\",\"operacionExitosa\":false,\"operacionObservaciones\":\"La cantidad necesaria para operar debe ser mayor a 0.\"}";

        assertThat(result.toString()).isEqualTo(expectedResult);
    }

}
