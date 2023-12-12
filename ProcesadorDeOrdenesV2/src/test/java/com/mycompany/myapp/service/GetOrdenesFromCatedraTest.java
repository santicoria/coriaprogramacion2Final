package com.mycompany.myapp.service;

import com.mycompany.myapp.UnitTest;
import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.repository.OrdenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


class GetOrdenesFromCatedraTest {


//    @Test
//    void getOrdenesFromCatedraTest(){
//
//        OrdenService ordenService = mock(OrdenService.class);
//        LoggerService loggerService = mock(LoggerService.class);
//
//        GetOrdenesFromCatedraService getOrdenesFromCatedraService = new GetOrdenesFromCatedraService(new RestTemplate(), ordenService, loggerService);
//
//        int databaseSizeBeforeCreate = ordenRepository.findAll().collectList().block().size();
//
//        getOrdenesFromCatedraService.getOrdenesCatedra("http://192.168.194.254:8000", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYW50aWFnb2NvcmlhIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTczMjYzMTcwM30.F1kI20s9p1kv8l2LhJcEcL-66_9X44zIybZw1piDV_ze2FiU3C7Th6iD6FRT7RFwuE9lWw1BCCJYr9hQYk8rEg");
//
//        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
//        assertThat(ordenList).hasSize(databaseSizeBeforeCreate + 1);
//
//
//
//    }
}
