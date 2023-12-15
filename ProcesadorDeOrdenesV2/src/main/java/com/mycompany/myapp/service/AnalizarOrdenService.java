package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.repository.OrdenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@Service
public class AnalizarOrdenService {

    private final CleanerService cleanerService;

    private final ProgramarOperacionService programarOperacionService;

    private final LoggerService loggerService;

    private ReporteOperacionesService reporteOperacionesService;

    private List<ObjectNode> listaUpdated= new ArrayList<>();

    private DataVerificationService dataVerificationService;

    private CompraVentaService compraVentaService;

    @Value("${external.service.url}")
    private String externalServiceUrl;

    @Value("${externalBearer.token}")
    private String bearerToken;


    public AnalizarOrdenService(CleanerService cleanerService, DataVerificationService dataVerificationService, CompraVentaService compraVentaService, ReporteOperacionesService reporteOperacionesService, LoggerService loggerService, ProgramarOperacionService programarOperacionService) {
        this.cleanerService = cleanerService;
        this.dataVerificationService = dataVerificationService;
        this.compraVentaService = compraVentaService;
        this.reporteOperacionesService = reporteOperacionesService;
        this.loggerService = loggerService;
        this.programarOperacionService = programarOperacionService;
    }


    public boolean analizarOrdenes(Mono<List<Orden>> operacionesPendientesMono){
        List<Long> idList= new ArrayList<Long>();
        List<Orden> operacionesPendientes = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ObjectNode wrapperJson = objectMapper.createObjectNode();

        operacionesPendientesMono.subscribe(
            list -> {
                for (int i=0; i<list.size(); i++) {
                    operacionesPendientes.add(list.get(i));
                }
            }
        );

        for (int i=0; i<operacionesPendientes.size(); i++){
            Orden orden = operacionesPendientes.get(i);
            idList.add(orden.getId());
            int code = analyzer(operacionesPendientes.get(i));
            codeHandler(code, operacionesPendientes.get(i));
            if(code<7){
                wrapperJson.putArray("ordenes").addAll(listaUpdated);

                //        reporteOperacionesService.reportarOperacionACatedra(wrapperJson);
                reporteOperacionesService.reportarOperacionInterno(wrapperJson).subscribe();
            }
            loggerService.logOrdenProcesada(orden);
        }

        cleanerService.cleanDb(idList);

        return true;
    }


    public int analyzer(Orden orden){

        Boolean accion = dataVerificationService.checkAccion(orden.getAccionId(), externalServiceUrl, bearerToken);
        Boolean cliente = dataVerificationService.checkCliente(orden.getCliente(), externalServiceUrl, bearerToken);
//        int time = java.time.LocalDateTime.now().getHour();
        int time = 12; //   En produccion esta linea no deberia ir, sino la de arriba
        int cantidad = orden.getCantidad();
        String modo = orden.getModo();
        String operacion = orden.getOperacion();


        if(time<9 || time>18){
            return 1;
        } else if (!accion) {
            return 2;
        } else if (!cliente) {
            return 3;
        } else if (cantidad<=0) {
            return 4;
        }else {
            if(modo.equals("AHORA")){
                if(operacion.equals("COMPRA")){
                    return 5;
                } else if (operacion.equals("VENTA")) {
                    return 6;
                }
            }else {
                return 7;
            }
        }
        return 8;
    }

    public void codeHandler(int code, Orden orden){
        if(code<=4){
            listaUpdated.add(errorCodeHandler(code, orden));
        } else if(code==5 || code==6){
            listaUpdated.add(ejecutarOperacion(code, orden));
        } else if (code==7) {
            programarOperacionService.programarOperacion(orden);
        }else {
            ;
        }

    }


    public ObjectNode errorCodeHandler(int code, Orden orden){

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ObjectNode existingJson = objectMapper.convertValue(orden, ObjectNode.class);
        existingJson.put("operacionExitosa", false);

        if(code==1){
            existingJson.put("operacionObservaciones", "La orden fue procesada fuera del horario permitido.");
        } else if (code==2) {
            existingJson.put("operacionObservaciones", "La accion no esta disponible.");
        }else if (code==3) {
            existingJson.put("operacionObservaciones", "El cliente no esta disponible.");
        }else if (code==4) {
            existingJson.put("operacionObservaciones", "La cantidad necesaria para operar debe ser mayor a 0.");
        }

        return existingJson;
    }

    public ObjectNode ejecutarOperacion(int code, Orden orden){

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ObjectNode existingJson = objectMapper.convertValue(orden, ObjectNode.class);
        existingJson.put("operacionExitosa", true);

        if(code==5){
            compraVentaService.comprarAccion(orden);
            existingJson.put("operacionObservaciones", "Compra exitosa!");
        } else if (code==6) {
            compraVentaService.venderAccion(orden);
            existingJson.put("operacionObservaciones", "Venta exitosa!");
        }

        return existingJson;
    }

}
