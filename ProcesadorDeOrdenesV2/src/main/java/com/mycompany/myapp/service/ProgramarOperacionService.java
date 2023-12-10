package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.myapp.domain.Orden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ProgramarOperacionService {

    private final ReporteOperacionesService reporteOperacionesService;

    private final CompraVentaService compraVentaService;

    private final LoggerService loggerService;

    private final Logger log = LoggerFactory.getLogger(ProgramarOperacionService.class);


    public ProgramarOperacionService(ReporteOperacionesService reporteOperacionesService, LoggerService loggerService, CompraVentaService compraVentaService) {
        this.reporteOperacionesService = reporteOperacionesService;
        this.loggerService = loggerService;
        this.compraVentaService = compraVentaService;
    }

    public void programarOperacion(Orden orden) {
        if(orden.getModo().equals("PRINCIPIODIA")){
            programPrincipioDia(orden);
        } else if (orden.getModo().equals("FINDIA")) {
            programFinDia(orden);
        }
    }

    private void programFinDia(Orden orden){

        System.out.println("----------> Orden programada para Fin del Dia <----------");
        loggerService.logOrdenProgramada(orden);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            ejecutarOperacion(orden);
        };

        long delay = calculateDelayToNextExecution(18);
        executor.schedule(task, delay, TimeUnit.MILLISECONDS);

    }

    private void programPrincipioDia(Orden orden){

        System.out.println("----------> Orden programada para Principio del Dia <----------");
        loggerService.logOrdenProgramada(orden);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            ejecutarOperacion(orden);
        };

        long delay = calculateDelayToNextExecution(9);
        executor.schedule(task, delay, TimeUnit.MILLISECONDS);

    }

    private void ejecutarOperacion(Orden orden){
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ObjectNode existingJson = objectMapper.convertValue(orden, ObjectNode.class);
        ObjectNode wrapperJson = objectMapper.createObjectNode();
        List<ObjectNode> listaUpdated= new ArrayList<>();

        String operacion = orden.getOperacion();

        listaUpdated.add(existingJson);
        wrapperJson.putArray("ordenes").addAll(listaUpdated);

        if(operacion.equals("COMPRA")){
            log.debug("Compra exitosa! --------> " + compraVentaService.comprarAccion(orden));
            existingJson.put("operacionExitosa", true);
            existingJson.put("operacionObservaciones", "Compra exitosa!");
        } else if (operacion.equals("VENTA")) {
            log.debug("Compra exitosa! --------> " + compraVentaService.venderAccion(orden));
            existingJson.put("operacionExitosa", true);
            existingJson.put("operacionObservaciones", "Venta exitosa!");
        }

        reporteOperacionesService.reportarOperacion(wrapperJson);

    }

    private static long calculateDelayToNextExecution(long time) {
        long now = System.currentTimeMillis();
        long desiredTime = time;
//        long delay = desiredTime - now;
        long delay = 10000;
        return delay;
    }

}
