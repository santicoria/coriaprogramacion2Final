package com.mycompany.myapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import org.springframework.web.client.RestTemplate;

@Service
public class DataVerificationService {

    private final RestTemplate restTemplate;

    @Value("${external.service.url}")
    private String externalServiceUrl;

    @Value("${externalBearer.token}")
    private String bearerToken;

    public DataVerificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    public Boolean checkCliente(int clienteId) {

        String externalEndpointClientes = "/api/clientes/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);

        try {
            HttpEntity<String> requestClienteEntity = new HttpEntity<>(headers);
            ResponseEntity<String> clienteData = restTemplate.exchange(
                externalServiceUrl + externalEndpointClientes + clienteId,
                HttpMethod.GET,
                requestClienteEntity,
                String.class
            );

            return Boolean.TRUE;

        }catch (HttpStatusCodeException e) {
            return Boolean.FALSE;
        }

    }

    public Boolean checkAccion(int accionId) {

        String externalEndpointAcciones = "/api/accions/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);

        HttpEntity<String> requestAccionesEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> accionData = restTemplate.exchange(
                externalServiceUrl + externalEndpointAcciones + accionId,
                HttpMethod.GET,
                requestAccionesEntity,
                String.class
            );
            return Boolean.TRUE;

        }catch (HttpStatusCodeException e) {
            return Boolean.FALSE;
        }
    }


}
