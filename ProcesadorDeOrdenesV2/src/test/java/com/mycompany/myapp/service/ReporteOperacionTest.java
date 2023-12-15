package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.myapp.domain.Orden;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReporteOperacionTest {

    RestTemplate restTemplate = new RestTemplate();
    ReporteService mockedReporteService = mock(ReporteService.class);
    ReporteOperacionesService reporteOperacionesService = new ReporteOperacionesService(restTemplate, mockedReporteService);


    public Object objectProvider(){

        Orden orden = new Orden()
            .cliente(201225)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ObjectNode wrapperJson = objectMapper.createObjectNode();
        List<ObjectNode> listaUpdated= new ArrayList<>();

        for(int i=0; i<2; i++){
            ObjectNode existingJson = objectMapper.convertValue(orden, ObjectNode.class);
            existingJson.put("operacionExitosa", true);
            existingJson.put("operacionObservaciones", "Compra exitosa!");
            listaUpdated.add(existingJson);
            wrapperJson.putArray("ordenes").addAll(listaUpdated);
        }

        return wrapperJson;

    }

    public ObjectNode objectProviderAsNode(){

        Orden orden = new Orden()
            .cliente(201225)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ObjectNode wrapperJson = objectMapper.createObjectNode();
        List<ObjectNode> listaUpdated= new ArrayList<>();

        for(int i=0; i<2; i++){
            ObjectNode existingJson = objectMapper.convertValue(orden, ObjectNode.class);
            existingJson.put("operacionExitosa", true);
            existingJson.put("operacionObservaciones", "Compra exitosa!");
            listaUpdated.add(existingJson);
            wrapperJson.putArray("ordenes").addAll(listaUpdated);
        }

        return wrapperJson;

    }


    @Test
    void reportarOperacionACatedra(){

        Object ordenes = objectProvider();

        ResponseEntity<String> response = reporteOperacionesService.reportarOperacionACatedra(ordenes);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void reportarOperacionInterno(){

        ObjectNode ordenes = objectProviderAsNode();

        ReporteOperacionesService mockedReporteOperacionesService = mock(ReporteOperacionesService.class);
        when(mockedReporteOperacionesService.reportarOperacionInterno(ordenes)).thenReturn(Mono.empty());

        Mono<Void> result = mockedReporteOperacionesService.reportarOperacionInterno(ordenes);

        assertThat(result).isNotNull();


    }
}
