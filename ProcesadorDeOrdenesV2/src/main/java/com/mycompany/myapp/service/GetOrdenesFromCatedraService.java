package com.mycompany.myapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.myapp.service.dto.OrdenDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GetOrdenesFromCatedraService {

    private final LoggerService loggerService;

    private final RestTemplate restTemplate;

    private final OrdenService ordenService;

    public GetOrdenesFromCatedraService(RestTemplate restTemplate, OrdenService ordenService, LoggerService loggerService) {
        this.restTemplate = restTemplate;
        this.ordenService = ordenService;
        this.loggerService = loggerService;
    }


    public Mono<Void> getOrdenesCatedra(String externalServiceUrl, String bearerToken){

        List<OrdenDTO> ordenes = getOrdenes(externalServiceUrl, bearerToken);

        return saveOrdenes(ordenes);

    }

    public List<OrdenDTO> getOrdenes(String externalServiceUrl, String bearerToken){
        String externalEndpoint = "/api/ordenes/ordenes";

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        List<OrdenDTO> ordenDTOList;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);

        //  Get Request a la catedra para obtener la lista de ordenes sin procesar
        HttpEntity<String> requestGetEntity = new HttpEntity<>(headers);
        ResponseEntity<String> jsonData = restTemplate.exchange(
            externalServiceUrl + externalEndpoint,
            HttpMethod.GET,
            requestGetEntity,
            String.class
        );

//          Transformar el JSON del Request a una lista de OrdenDTO
            try {
                ordenDTOList = mapper.readValue(jsonData.getBody().substring(11, jsonData.getBody().length() - 1), new TypeReference<List<OrdenDTO>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        return ordenDTOList;

    }

    public Mono<Void> saveOrdenes(List<OrdenDTO> ordenes){

        loggerService.logOrdenRecuperada(ordenes);

        return  ordenService.saveAll(ordenes);

    }


}
