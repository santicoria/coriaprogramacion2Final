package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReporteOperacionesService {

    private final Logger log = LoggerFactory.getLogger(ReporteOperacionesService.class);

    private final RestTemplate restTemplate;
    @Value("${external.service.url}")
    private String externalServiceUrl;

    @Value("${externalBearer.token}")
    private String bearerToken;

    public ReporteOperacionesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void reportarOperacion(Object operacion){
        String externalEndpoint = "/api/reporte-operaciones/reportar";

        System.out.println("Paquete actualizado -----------> " + operacion);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + bearerToken);

        HttpEntity<Object> requestPostEntity = new HttpEntity<>(operacion, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            externalServiceUrl + externalEndpoint,
            HttpMethod.POST,
            requestPostEntity,
            String.class
        );

        if(response.getStatusCode() == HttpStatus.OK){
            log.debug("Operacion reportada con exito.");
        }else {
            log.debug("Error al reportar.");
        }

    }

}
