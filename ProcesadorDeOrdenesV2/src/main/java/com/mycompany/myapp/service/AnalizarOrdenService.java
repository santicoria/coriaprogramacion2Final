package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.repository.OrdenRepository;
import com.mycompany.myapp.service.mapper.OrdenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@Service
public class AnalizarOrdenService {

    private final ProgramarOperacionService programarOperacionService;

    private final LoggerService loggerService;

    private final Logger log = LoggerFactory.getLogger(AnalizarOrdenService.class);

    private ReporteOperacionesService reporteOperacionesService;

    private OrdenRepository ordenRepository;

    private List<Orden> operacionFallida = new ArrayList<>();

    private List<Orden> operacionExitosa = new ArrayList<>();

    private DataVerificationService dataVerificationService;

    private CompraVentaService compraVentaService;

    @Value("${external.service.url}")
    private String externalServiceUrl;

    @Value("${externalBearer.token}")
    private String bearerToken;


    public AnalizarOrdenService(OrdenRepository ordenRepository, DataVerificationService dataVerificationService, CompraVentaService compraVentaService, ReporteOperacionesService reporteOperacionesService, LoggerService loggerService, ProgramarOperacionService programarOperacionService) {
        this.ordenRepository = ordenRepository;
        this.dataVerificationService = dataVerificationService;
        this.compraVentaService = compraVentaService;
        this.reporteOperacionesService = reporteOperacionesService;
        this.loggerService = loggerService;
        this.programarOperacionService = programarOperacionService;
    }


    public boolean analizarOrdenes(Mono<List<Orden>> operacionesPendientesMono) {

        List<Orden> operacionesPendientes = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ObjectNode wrapperJson = objectMapper.createObjectNode();

        List<ObjectNode> listaUpdated= new ArrayList<>();
//        Mono<List<Orden>> operacionesPendientesMono = ordenRepository.findAll().collectList();

        operacionesPendientesMono.subscribe(
            list -> {
                for (int i=0; i<list.size(); i++) {
                    operacionesPendientes.add(list.get(i));
                }
            }
        );

        System.out.println("Largo ------------> " + operacionesPendientes.size());
        System.out.println("Contenido ------------> " + operacionesPendientes);


        for (int i=0; i<operacionesPendientes.size(); i++){

            Orden orden = operacionesPendientes.get(i);

            Boolean accion = dataVerificationService.checkAccion(orden.getAccionId());
            Boolean cliente = dataVerificationService.checkCliente(orden.getCliente());
//            int time = java.time.LocalDateTime.now().getHour();
            int time = 12;  //  Cambiar en final
            int cantidad = orden.getCantidad();
            String modo = orden.getModo();
            String operacion = orden.getOperacion();

            ObjectNode existingJson = objectMapper.convertValue(orden, ObjectNode.class);


            if(time<9 || time>18){
                log.debug("La orden fue procesada fuera del horario permitido.");
                operacionFallida.add(operacionesPendientes.get(i));
                existingJson.put("operacionExitosa", false);
                existingJson.put("operacionObservaciones", "La orden fue procesada fuera del horario permitido.");
            } else if (!accion) {
                log.debug("La accion no esta disponible.");
                operacionFallida.add(operacionesPendientes.get(i));
                existingJson.put("operacionExitosa", false);
                existingJson.put("operacionObservaciones", "La accion no esta disponible.");
            } else if (!cliente) {
                log.debug("El cliente no esta disponible.");
                operacionFallida.add(operacionesPendientes.get(i));
                existingJson.put("operacionExitosa", false);
                existingJson.put("operacionObservaciones", "El cliente no esta disponible.");
            } else if (cantidad<=0) {
                log.debug("La cantidad necesaria para operar debe ser mayor a 0.");
                operacionFallida.add(operacionesPendientes.get(i));
                existingJson.put("operacionExitosa", false);
                existingJson.put("operacionObservaciones", "La cantidad necesaria para operar debe ser mayor a 0.");
            }else {
                log.debug("Es legal ------------>  Hora: " + time + " Accion: " + accion + " Cliente: " + cliente + " Modo: " + modo);
                operacionExitosa.add(orden);
                if(modo.equals("AHORA")){
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
                }else {
                    programarOperacionService.programarOperacion(orden);
                }

            }

            System.out.println("Operacion -----------> " + operacionesPendientes.get(i));
            System.out.println("ExistingJson -----------> " + existingJson);
            System.out.println("listaUpdated -----------> " + listaUpdated);
            System.out.println("Paquete actualizado -----------> " + wrapperJson);



            log.info("Orden procesada ------------> " + operacionesPendientes.get(i));
            loggerService.logOrdenProcesada(operacionesPendientes.get(i));

        }

        reporteOperacionesService.reportarOperacion(wrapperJson);
        return true;
    }
}
