package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.repository.OrdenRepository;
import com.mycompany.myapp.service.dto.OrdenDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class GetOrdenesFromCatedraTest {

    RestTemplate restTemplate = new RestTemplate();
    LoggerService mockedLoggerService = mock(LoggerService.class);
    OrdenService mockedOrdenService = mock(OrdenService.class);
    GetOrdenesFromCatedraService getOrdenesFromCatedraService = new GetOrdenesFromCatedraService(restTemplate, mockedOrdenService, mockedLoggerService);

    String url = "http://192.168.194.254:8000";
    String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYW50aWFnb2NvcmlhIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTczMjYzMTcwM30.F1kI20s9p1kv8l2LhJcEcL-66_9X44zIybZw1piDV_ze2FiU3C7Th6iD6FRT7RFwuE9lWw1BCCJYr9hQYk8rEg";


    @Test
    void getOrdenesCatedraTest(){

        //  Cambiar segun el resultado esperado
        String expectedResult = "[OrdenDTO{id=null, cliente=201225, accionId=13, accion='PAM', operacion='COMPRA', precio=127.78, cantidad=20, fechaOperacion='2023-12-15T04:00', modo='PRINCIPIODIA'}, OrdenDTO{id=null, cliente=201225, accionId=13, accion='PAM', operacion='COMPRA', precio=119.17, cantidad=32, fechaOperacion='2023-12-15T12:00', modo='AHORA'}, OrdenDTO{id=null, cliente=201225, accionId=13, accion='PAM', operacion='VENTA', precio=122.96, cantidad=12, fechaOperacion='2023-12-15T14:20', modo='AHORA'}, OrdenDTO{id=null, cliente=201225, accionId=13, accion='PAM', operacion='VENTA', precio=118.23, cantidad=10, fechaOperacion='2023-12-15T18:00', modo='FINDIA'}]";

        List<OrdenDTO> getResult = getOrdenesFromCatedraService.getOrdenes(url, token);

        assertThat(getResult.toString()).isEqualTo(expectedResult);
    }

    @Test
    void saveOrdenes(){

        List<OrdenDTO> ordenes = getOrdenesFromCatedraService.getOrdenes(url, token);

        GetOrdenesFromCatedraService mockedGetOrdenesFromCatedraService = mock(GetOrdenesFromCatedraService.class);
        when(mockedGetOrdenesFromCatedraService.saveOrdenes(ordenes)).thenReturn(Mono.empty());

        Mono<Void> result = mockedGetOrdenesFromCatedraService.saveOrdenes(ordenes);

        assertThat(result).isNotNull();

    }


}
