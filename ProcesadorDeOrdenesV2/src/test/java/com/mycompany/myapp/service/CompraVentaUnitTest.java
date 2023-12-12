package com.mycompany.myapp.service;


import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.repository.OrdenRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;



public class CompraVentaUnitTest {

    @Test
    void comprarAccion(){

        Orden ordenCompra = new Orden()
            .cliente(12313)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");

        CompraVentaService compraVentaService = new CompraVentaService();

        boolean resultado = compraVentaService.comprarAccion(ordenCompra);
        assertThat(resultado).isTrue();
    }

    @Test
    void venderAccion(){

        Orden ordenVenta = new Orden()
            .cliente(12313)
            .accionId(1)
            .accion("AAPL")
            .operacion("VENTA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");

        CompraVentaService compraVentaService = new CompraVentaService();

        boolean resultado = compraVentaService.venderAccion(ordenVenta);
        assertThat(resultado).isTrue();
    }
}
