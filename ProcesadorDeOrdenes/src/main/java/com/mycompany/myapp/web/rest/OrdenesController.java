package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.OrdenRepository;
import com.mycompany.myapp.service.AnalizarOrdenService;
import com.mycompany.myapp.service.OrdenService;
import com.mycompany.myapp.service.mapper.OrdenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api/ordenes")
public class OrdenesController {

    private final AnalizarOrdenService analizarOrdenService;
    private final OrdenRepository ordenRepository;
    private final OrdenMapper ordenMapper;

    private final RestTemplate restTemplate;

    private final OrdenService ordenService;

    @Value("${external.service.url}")
    private String externalServiceUrl;

    @Value("${bearer.token}")
    private String bearerToken;

    @Value("${bearer1.token}")
    private String bearerLocalToken;


    @Autowired
    public OrdenesController(AnalizarOrdenService analizarOrdenService, OrdenRepository ordenRepository, RestTemplate restTemplate, OrdenService ordenService, OrdenMapper ordenMapper) {
        this.analizarOrdenService = analizarOrdenService;
        this.ordenRepository = ordenRepository;
        this.restTemplate = restTemplate;
        this.ordenService = ordenService;
        this.ordenMapper = ordenMapper;
    }



    @GetMapping("/ordenes")
    public ResponseEntity<String> getOrdenes() {

        String externalEndpoint = "/api/ordenes/ordenes";
        String postUrl = "http://localhost:8081/api/ordens";

        HttpHeaders headersGet = new HttpHeaders();
        headersGet.set("Authorization", "Bearer " + bearerToken);

        HttpEntity<String> requestGetEntity = new HttpEntity<>(headersGet);
        ResponseEntity<String> jsonData = restTemplate.exchange(
            externalServiceUrl + externalEndpoint,
            HttpMethod.GET,
            requestGetEntity,
            String.class
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + bearerLocalToken);

        JSONObject json = new JSONObject(jsonData.getBody());

        for (int i = 0; i<json.getJSONArray("ordenes").length()-1; i++) {
            System.out.println("Iteracion ----> " + i);
            String ordeness = json.getJSONArray("ordenes").get(i).toString();

            HttpEntity<String> requestEntity = new HttpEntity<>(ordeness.replaceAll("[\\[\\]]", ""), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                postUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );

        }

        HttpEntity<String> requestEntity = new HttpEntity<>(json.getJSONArray("ordenes").get(json.getJSONArray("ordenes").length()-1).toString().replaceAll("[\\[\\]]", ""), headers);

        ResponseEntity<String> response = restTemplate.exchange(
            postUrl,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {

            return response;

        } else {
            // Handle error or return null/throw exception
            return null;
        }

    }

    @GetMapping("/analizar-ordenes")
    public String analizarOrdenes() {

        analizarOrdenService.analizarOrdenes();

        return "Ok";
    }

}
