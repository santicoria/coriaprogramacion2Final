package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.service.dto.OrdenDTO;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


@Service
public class LoggerService {


    private final Logger loggerRecuperadas = Logger.getLogger("loggerRecuperadas");

    private final Logger loggerProcesadas = Logger.getLogger("loggerProcesadas");

    private final Logger loggerProgramadas = Logger.getLogger("loggerProgramadas");

    private FileHandler recuperadaFh = null;
    private FileHandler procesadaFh = null;
    private FileHandler programadaFh = null;

    public void LoggingTester() {

        SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
        try {
            recuperadaFh = new FileHandler(System.getProperty("user.dir") + "/Logs/LogOrdenesRecuperadas.log", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            procesadaFh = new FileHandler(System.getProperty("user.dir") + "/Logs/LogOrdenesProcesadas.log", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            programadaFh = new FileHandler(System.getProperty("user.dir") + "/Logs/LogOrdenesProgramadas.log", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        recuperadaFh.setFormatter(new SimpleFormatter());
        loggerRecuperadas.addHandler(recuperadaFh);

        procesadaFh.setFormatter(new SimpleFormatter());
        loggerProcesadas.addHandler(procesadaFh);

        programadaFh.setFormatter(new SimpleFormatter());
        loggerProgramadas.addHandler(programadaFh);
    }

    public void logOrdenRecuperada(List<OrdenDTO> orden) {
        loggerRecuperadas.info("Ordenes recuperadas de la catedra: " + orden);
    }

    public void logOrdenProcesada(Orden orden) {
        loggerProcesadas.info("Orden procesada: " + orden);
    }

    public void logOrdenProgramada(Orden orden) {
        loggerProgramadas.info("Orden programada: " + orden);
    }

}
