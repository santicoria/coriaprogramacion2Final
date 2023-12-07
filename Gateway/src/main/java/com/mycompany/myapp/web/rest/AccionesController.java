package com.mycompany.myapp.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.queryParam;

@RestController
@RequestMapping("/api/redirect/acciones")
public class AccionesController {

    private final RestTemplate restTemplate;

    @Value("${external.service.url}")
    private String externalServiceUrl; // External service URL

    @Value("${bearer.token}")
    private String bearerToken; // Bearer Token

    @Autowired
    public AccionesController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<Object> getAcciones() {
        String externalEndpoint = "/api/accions"; // Replace with the specific path on the external service

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken); // Add Bearer Token to header

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Forward the request to the external service with the token in the header
        ResponseEntity<Object> response = restTemplate.exchange(
            externalServiceUrl + externalEndpoint,
            HttpMethod.GET,
            entity,
            Object.class
        );
        return response;
    }

    @GetMapping(value = "/buscar")
    public ResponseEntity<Object> getAccionesQuery(
        @RequestParam(required = false) final String empresa,
        @RequestParam(required = false) final String codigo) {


        String externalEndpoint = "/api/acciones/buscar"; // Replace with the specific path on the external service

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(externalServiceUrl + externalEndpoint);

        // Add query parameters if they exist
        if (empresa != null) {
            builder.queryParam("empresa", empresa);
        }
        if (codigo != null) {
            builder.queryParam("codigo", codigo);
        }

        // Build the URI with query parameters
        String uri = builder.toUriString();


        System.out.println(uri);

        // Set Bearer Token in the header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Forward the request to the external service with the token in the header and query parameters
        ResponseEntity<Object> response = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            entity,
            Object.class
        );
        return response;
    }

    @GetMapping(value = "/ultimovalor/{codigo}")
    public ResponseEntity<Object> getUltimoValor(@PathVariable String codigo) {

        String externalEndpoint = "/api/acciones/ultimovalor/"; // Replace with the specific path on the external service

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(externalServiceUrl + externalEndpoint + codigo);

        // Build the URI with query parameters
        String uri = builder.toUriString();


        // Set Bearer Token in the header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Forward the request to the external service with the token in the header and query parameters
        ResponseEntity<Object> response = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            entity,
            Object.class
        );
        return response;
    }

    @GetMapping(value = "/valores/{codigo}/{inicio}/{fin}")
    public ResponseEntity<Object> getValores(
        @PathVariable String codigo,
        @PathVariable String inicio,
        @PathVariable String fin
    ) {

        String externalEndpoint = "/api/acciones/valores/"; // Replace with the specific path on the external service

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(externalServiceUrl + externalEndpoint + codigo + "/" + inicio + "/" + fin);

        // Build the URI with query parameters
        String uri = builder.toUriString();


        // Set Bearer Token in the header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Forward the request to the external service with the token in the header and query parameters
        ResponseEntity<Object> response = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            entity,
            Object.class
        );
        return response;
    }

}
