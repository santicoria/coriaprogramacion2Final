package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Orden;
import org.springframework.stereotype.Service;

@Service
public class CompraVentaService {


    public boolean comprarAccion(Orden orden) {
        System.out.println("-------> Compra Exitosa! <-------");
        return true;
    }

    public boolean venderAccion(Orden orden) {
        System.out.println("-------> Venta Exitosa! <-------");
        return true;
    }

}
