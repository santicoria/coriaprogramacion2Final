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

    public DataVerificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    public Boolean checkCliente(int clienteId, String externalServiceUrl, String bearerToken) {

        String externalEndpointClientes = "/api/clientes/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYW50aWFnb2NvcmlhIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTczMjYzMTcwM30.F1kI20s9p1kv8l2LhJcEcL-66_9X44zIybZw1piDV_ze2FiU3C7Th6iD6FRT7RFwuE9lWw1BCCJYr9hQYk8rEg");

        try {
            HttpEntity<String> requestClienteEntity = new HttpEntity<>(headers);
            ResponseEntity<String> clienteData = restTemplate.exchange(
                "http://192.168.194.254:8000" + externalEndpointClientes + clienteId,
                HttpMethod.GET,
                requestClienteEntity,
                String.class
            );

            return Boolean.TRUE;

        }catch (HttpStatusCodeException e) {
            return Boolean.FALSE;
        }

    }

    public Boolean checkAccion(int accionId, String externalServiceUrl, String bearerToken) {

        String externalEndpointAcciones = "/api/accions/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYW50aWFnb2NvcmlhIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTczMjYzMTcwM30.F1kI20s9p1kv8l2LhJcEcL-66_9X44zIybZw1piDV_ze2FiU3C7Th6iD6FRT7RFwuE9lWw1BCCJYr9hQYk8rEg");

        HttpEntity<String> requestAccionesEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> accionData = restTemplate.exchange(
                "http://192.168.194.254:8000" + externalEndpointAcciones + accionId,
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
