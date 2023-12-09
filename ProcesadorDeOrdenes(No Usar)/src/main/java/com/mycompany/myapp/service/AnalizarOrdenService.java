package com.mycompany.myapp.service;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.repository.OrdenRepository;
import com.mycompany.myapp.service.mapper.OrdenMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AnalizarOrdenService {

    private OrdenRepository ordenRepository;

    private final OrdenMapper ordenMapper;

    private List<Orden> operacionFallida = new ArrayList<>();

    private List<Orden> operacionExitosa = new ArrayList<>();

    private final RestTemplate restTemplate;

    @Value("${external.service.url}")
    private String externalServiceUrl;

    @Value("${bearer.token}")
    private String bearerToken;


    public AnalizarOrdenService(OrdenRepository ordenRepository, OrdenMapper ordenMapper, RestTemplate restTemplate) {
        this.ordenRepository = ordenRepository;
        this.ordenMapper = ordenMapper;
        this.restTemplate = restTemplate;
    }


    public void analizarOrdenes() {

        String externalEndpointAcciones = "/api/accions/";
        String externalEndpointClientes = "/api/clientes/";
        String externalEndpointPost = "/api/reporte-operaciones/reportar";

        Boolean accion = Boolean.FALSE;
        Boolean cliente = Boolean.FALSE;




        List<Orden> operacionesPendientes = new ArrayList<>();
        Mono<List<Orden>> operacionesPendientesMono = ordenRepository.findAll().collectList();

            operacionesPendientesMono.subscribe(
            list -> {
                for (int i=0; i<list.size(); i++) {
                    operacionesPendientes.add(list.get(i));
                }
            }
        );

        System.out.println("Largo ------------> " + operacionesPendientes.size());
        System.out.println("Contenido ------------> " + operacionesPendientes);



        HttpHeaders headersGet = new HttpHeaders();
        headersGet.set("Authorization", "Bearer " + bearerToken);


        for (int i=0; i<operacionesPendientes.size(); i++){
            System.out.println("Orden ------------>  " + operacionesPendientes.get(i));

            int time = operacionesPendientes.get(i).getFechaOperacion().getHour();
            int cantidad = operacionesPendientes.get(i).getCantidad();
            String modo = operacionesPendientes.get(i).getModo();

            HttpEntity<String> requestAccionesEntity = new HttpEntity<>(headersGet);


            try {
                ResponseEntity<String> accionData = restTemplate.exchange(
                    externalServiceUrl + externalEndpointAcciones + operacionesPendientes.get(i).getAccionId(),
                    HttpMethod.GET,
                    requestAccionesEntity,
                    String.class
                );
                accion = Boolean.TRUE;

            }catch (HttpStatusCodeException e) {
                accion = Boolean.FALSE;
            }


            try {
                HttpEntity<String> requestClienteEntity = new HttpEntity<>(headersGet);
                ResponseEntity<String> clienteData = restTemplate.exchange(
                    externalServiceUrl + externalEndpointClientes + operacionesPendientes.get(i).getCliente(),
                    HttpMethod.GET,
                    requestClienteEntity,
                    String.class
                );

                cliente = Boolean.TRUE;

            }catch (HttpStatusCodeException e) {
                cliente = Boolean.FALSE;
            }


            if(time>9 && time<18 && accion && cliente && cantidad>0){
                System.out.println("Es legal ------------>  Hora:" + time + " Accion: " + accion + " Cliente: " + cliente);
                operacionExitosa.add(operacionesPendientes.get(i));

                ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
                ObjectNode existingJson = objectMapper.convertValue(operacionesPendientes.get(i), ObjectNode.class);
                ObjectNode wrapperJson = objectMapper.createObjectNode();
                existingJson.put("operacionExitosa", true);
                existingJson.put("operacionObservaciones", "ok");

                List<ObjectNode> listaUpdated= new ArrayList<>();
                listaUpdated.add(existingJson);
                wrapperJson.putArray("ordenes").addAll(listaUpdated);



                System.out.println("Paquete actualizado -----------> " + wrapperJson);

                headersGet.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Object> requestPostEntity = new HttpEntity<>(wrapperJson, headersGet);
                ResponseEntity<String> response = restTemplate.exchange(
                    externalServiceUrl + externalEndpointPost,
                    HttpMethod.POST,
                    requestPostEntity,
                    String.class
                );

            }
            else {
                System.out.println("Es ilegal ------------> Hora:" + time + " Accion: " + accion + " Cliente: " + cliente);
                operacionFallida.add(operacionesPendientes.get(i));
            }
        }
    }


}
