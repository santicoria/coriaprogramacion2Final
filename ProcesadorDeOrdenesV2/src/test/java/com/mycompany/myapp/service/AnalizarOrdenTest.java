package com.mycompany.myapp.service;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.repository.EntityManager;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AnalizarOrdenTest {

    @Autowired
    private AnalizarOrdenService analizarOrdenService;

    private static final Integer DEFAULT_CLIENTE = 1;
    private static final Integer DEFAULT_CLIENTE2 = 2;

    private static final Integer DEFAULT_ACCION_ID = 1;
    private static final Integer DEFAULT_ACCION_ID2 = 2;

    private static final String DEFAULT_ACCION = "AAAAAAAAAA";
    private static final String DEFAULT_ACCION2 = "BBBBBBBBBB";

    private static final String DEFAULT_OPERACION = "AAAAAAAAAA";
    private static final String DEFAULT_OPERACION2 = "BBBBBBBBBB";

    private static final Float DEFAULT_PRECIO = 1F;
    private static final Float DEFAULT_PRECIO2 = 2F;

    private static final Integer DEFAULT_CANTIDAD = 1;
    private static final Integer DEFAULT_CANTIDAD2 = 2;

    private static final LocalDateTime DEFAULT_FECHA_OPERACION = LocalDateTime.ofEpochSecond(0,0, ZoneOffset.MIN);
    private static final LocalDateTime DEFAULT_FECHA_OPERACION2 = LocalDateTime.now(ZoneId.systemDefault());

    private static final String DEFAULT_MODO = "AAAAAAAAAA";
    private static final String DEFAULT_MODO2 = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ordens";
    private static final String ENTITY_API_URL2 = ENTITY_API_URL + "/{id}";

    private Orden orden;

    @Autowired
    private EntityManager em;

    public static Orden createEntity(EntityManager em) {
        Orden orden = new Orden()
            .cliente(DEFAULT_CLIENTE)
            .accionId(DEFAULT_ACCION_ID)
            .accion(DEFAULT_ACCION)
            .operacion(DEFAULT_OPERACION)
            .precio(DEFAULT_PRECIO)
            .cantidad(DEFAULT_CANTIDAD)
            .fechaOperacion(DEFAULT_FECHA_OPERACION)
            .modo(DEFAULT_MODO);
        return orden;
    }
    public static Orden createEntity2(EntityManager em) {
        Orden orden = new Orden()
            .cliente(DEFAULT_CLIENTE2)
            .accionId(DEFAULT_ACCION_ID2)
            .accion(DEFAULT_ACCION2)
            .operacion(DEFAULT_OPERACION2)
            .precio(DEFAULT_PRECIO2)
            .cantidad(DEFAULT_CANTIDAD2)
            .fechaOperacion(DEFAULT_FECHA_OPERACION2)
            .modo(DEFAULT_MODO2);
        return orden;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Orden.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        orden = createEntity(em);
    }

//    @Test
//    void analizarOrdenes() throws Exception{
//
//        List<Orden> listaOrdenes = new ArrayList<>();
//
//        Orden orden1 = createEntity(em);
//        Orden orden2 = createEntity2(em);
//
//        listaOrdenes.add(orden1);
//        listaOrdenes.add(orden2);
//
//        boolean resultado = analizarOrdenService.analizarOrdenes(Mono.just(listaOrdenes));
//
//        assertThat(resultado).isTrue();
//    }
}
