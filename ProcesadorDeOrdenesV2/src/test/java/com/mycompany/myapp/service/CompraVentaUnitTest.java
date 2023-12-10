package com.mycompany.myapp.service;


import com.mycompany.myapp.UnitTest;
import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.OrdenRepository;
import liquibase.servicelocator.LiquibaseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@UnitTest
@ActiveProfiles("testcontainers")
@LiquibaseService
@LiquibaseDataSource
public class CompraVentaUnitTest {

    private CompraVentaService compraVentaService;

    private Orden ordenCompra;
    private Orden ordenVenta;

    @Autowired
    private EntityManager em;

    @Autowired
    private OrdenRepository ordenRepository;

    public static Orden createEntity(EntityManager em) {
        Orden ordenCompra = new Orden()
            .cliente(201225)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");
        return ordenCompra;
    }

    public static Orden createEntity2(EntityManager em) {
        Orden ordenVenta = new Orden()
            .cliente(201225)
            .accionId(1)
            .accion("AAPL")
            .operacion("COMPRA")
            .precio(12F)
            .cantidad(5)
            .fechaOperacion(LocalDateTime.now())
            .modo("AHORA");
        return ordenVenta;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Orden.class).block();
        } catch (Exception e) {
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        ordenCompra = createEntity(em);
        ordenVenta = createEntity2(em);
    }

    @Test
    void comprarAccion(){
        boolean resultado = compraVentaService.comprarAccion(ordenCompra);
        assertThat(resultado).isTrue();
    }

    @Test
    void venderAccion(){
        boolean resultado = compraVentaService.venderAccion(ordenVenta);
        assertThat(resultado).isTrue();
    }
}
