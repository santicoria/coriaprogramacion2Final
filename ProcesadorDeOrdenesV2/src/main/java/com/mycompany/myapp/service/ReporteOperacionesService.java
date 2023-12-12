package com.mycompany.myapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.myapp.service.dto.ReporteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteOperacionesService {

    private final ReporteService reporteService;

    private final Logger log = LoggerFactory.getLogger(ReporteOperacionesService.class);

    private final RestTemplate restTemplate;
    @Value("${external.service.url}")
    private String externalServiceUrl;

    @Value("${externalBearer.token}")
    private String bearerToken;

    public ReporteOperacionesService(RestTemplate restTemplate, ReporteService reporteService) {
        this.restTemplate = restTemplate;
        this.reporteService = reporteService;
    }

    public void reportarOperacionACatedra(Object operacion){

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

    public Mono<Void> reportarOperacionInterno(ObjectNode operacion){

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String operacionString = operacion.get("ordenes").toString().replaceAll("\"id\":([0-9]+),", "");
        List<ReporteDTO> op ;

        try {
            op = mapper.readValue(operacionString, new TypeReference<List<ReporteDTO>>() {});
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return reporteService.saveAll(op);

    }

}
