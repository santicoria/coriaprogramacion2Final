package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.myapp.domain.Orden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProgramarOperacionService {

    @Value("${external.service.url}")
    private String externalServiceUrl;

    @Value("${externalBearer.token}")
    private String bearerToken;

    private final RestTemplate restTemplate;

    private final ReporteOperacionesService reporteOperacionesService;

    private final CompraVentaService compraVentaService;

    private final LoggerService loggerService;

    private final Logger log = LoggerFactory.getLogger(ProgramarOperacionService.class);


    public ProgramarOperacionService(ReporteOperacionesService reporteOperacionesService, LoggerService loggerService, CompraVentaService compraVentaService, RestTemplate restTemplate) {
        this.reporteOperacionesService = reporteOperacionesService;
        this.loggerService = loggerService;
        this.compraVentaService = compraVentaService;
        this.restTemplate = restTemplate;
    }

    public String programarOperacion(Orden orden) {
        if(orden.getModo().equals("PRINCIPIODIA")){
            programPrincipioDia(orden);
            return "PRINCIPIODIA";
        } else if (orden.getModo().equals("FINDIA")) {
            programFinDia(orden);
            return "FINDIA";
        }
        return "ERROR";
    }

    public void programFinDia(Orden orden){

        loggerService.logOrdenProgramada(orden);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            ejecutarOperacion(orden);
        };

        long delay = calculateDelayToNextExecution(18);
        executor.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    public void programPrincipioDia(Orden orden){

        loggerService.logOrdenProgramada(orden);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            ejecutarOperacion(orden);
        };

        long delay = calculateDelayToNextExecution(9);
        executor.schedule(task, delay, TimeUnit.MILLISECONDS);

    }

    public void ejecutarOperacion(Orden orden){

        getCurrentPrice(orden);

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ObjectNode existingJson = objectMapper.convertValue(orden, ObjectNode.class);
        ObjectNode wrapperJson = objectMapper.createObjectNode();
        List<ObjectNode> listaUpdated= new ArrayList<>();

        String operacion = orden.getOperacion();

        listaUpdated.add(existingJson);
        wrapperJson.putArray("ordenes").addAll(listaUpdated);

        if(operacion.equals("COMPRA")){
            log.debug("Compra exitosa! --------> " + compraVentaService.comprarAccion(orden));
            existingJson.put("precio", getCurrentPrice(orden));
            existingJson.put("operacionExitosa", true);
            existingJson.put("operacionObservaciones", "Compra exitosa!");
        } else if (operacion.equals("VENTA")) {
            log.debug("Venta exitosa! --------> " + compraVentaService.venderAccion(orden));
            existingJson.put("precio", getCurrentPrice(orden));
            existingJson.put("operacionExitosa", true);
            existingJson.put("operacionObservaciones", "Venta exitosa!");
        }

        reporteOperacionesService.reportarOperacionACatedra(wrapperJson);
        reporteOperacionesService.reportarOperacionInterno(wrapperJson).subscribe();

    }

    public String getCurrentPrice(Orden orden){

        String externalEndpoint = "/api/acciones/ultimovalor/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);

        HttpEntity<String> requestGetEntity = new HttpEntity<>(headers);
        ResponseEntity<String> jsonData = restTemplate.exchange(
            externalServiceUrl + externalEndpoint + orden.getAccion(),
            HttpMethod.GET,
            requestGetEntity,
            String.class
        );
        String pattern = "\"valor\":\\s*([\\d.]+)";

        // Create a Pattern object
        Pattern regex = Pattern.compile(pattern);

        // Create a Matcher object
        Matcher matcher = regex.matcher(jsonData.getBody());

        // Find the match and extract the value after "valor"
        if (matcher.find()) {
            String valor = matcher.group(1);
            return valor;
        } else {
            System.out.println("No match found.");
        }

        return "No match";
    }

    public long calculateDelayToNextExecution(long time) {
        long now = System.currentTimeMillis();
        long desiredTime = time;
//        long delay = desiredTime - now;
        // La linea de abajo hace que las ordenes procesadas se ejecuten 10 segundos luego de procesada la orden. Comentar la linea de arriba para usarla.
        long delay = 10000;
        return delay;
    }



}
