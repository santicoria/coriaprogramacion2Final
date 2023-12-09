package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Orden;
import org.springframework.stereotype.Service;

@Service
public class CompraVentaService {


    public Boolean comprarAccion(Orden orden) {
        System.out.println("-------> Compra Exitosa! <-------");
        return Boolean.TRUE;
    }

    public Boolean venderAccion(Orden orden) {
        System.out.println("-------> Venta Exitosa! <-------");
        return Boolean.TRUE;
    }

}
