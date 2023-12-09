package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.OrdenRepository;
import com.mycompany.myapp.service.dto.OrdenDTO;
import com.mycompany.myapp.service.mapper.OrdenMapper;

import java.time.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link OrdenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrdenResourceIT {

    private static final Integer DEFAULT_CLIENTE = 1;
    private static final Integer UPDATED_CLIENTE = 2;

    private static final Integer DEFAULT_ACCION_ID = 1;
    private static final Integer UPDATED_ACCION_ID = 2;

    private static final String DEFAULT_ACCION = "AAAAAAAAAA";
    private static final String UPDATED_ACCION = "BBBBBBBBBB";

    private static final String DEFAULT_OPERACION = "AAAAAAAAAA";
    private static final String UPDATED_OPERACION = "BBBBBBBBBB";

    private static final Float DEFAULT_PRECIO = 1F;
    private static final Float UPDATED_PRECIO = 2F;

    private static final Integer DEFAULT_CANTIDAD = 1;
    private static final Integer UPDATED_CANTIDAD = 2;

    private static final LocalDateTime DEFAULT_FECHA_OPERACION = LocalDateTime.ofEpochSecond(0,0, ZoneOffset.MIN);
    private static final LocalDateTime UPDATED_FECHA_OPERACION = LocalDateTime.now(ZoneId.systemDefault());

    private static final String DEFAULT_MODO = "AAAAAAAAAA";
    private static final String UPDATED_MODO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ordens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private OrdenMapper ordenMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Orden orden;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
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

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orden createUpdatedEntity(EntityManager em) {
        Orden orden = new Orden()
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .precio(UPDATED_PRECIO)
            .cantidad(UPDATED_CANTIDAD)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO);
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

    @Test
    void createOrden() throws Exception {
        int databaseSizeBeforeCreate = ordenRepository.findAll().collectList().block().size();
        // Create the Orden
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Orden in the database
        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeCreate + 1);
        Orden testOrden = ordenList.get(ordenList.size() - 1);
        assertThat(testOrden.getCliente()).isEqualTo(DEFAULT_CLIENTE);
        assertThat(testOrden.getAccionId()).isEqualTo(DEFAULT_ACCION_ID);
        assertThat(testOrden.getAccion()).isEqualTo(DEFAULT_ACCION);
        assertThat(testOrden.getOperacion()).isEqualTo(DEFAULT_OPERACION);
        assertThat(testOrden.getPrecio()).isEqualTo(DEFAULT_PRECIO);
        assertThat(testOrden.getCantidad()).isEqualTo(DEFAULT_CANTIDAD);
        assertThat(testOrden.getFechaOperacion()).isEqualTo(DEFAULT_FECHA_OPERACION);
        assertThat(testOrden.getModo()).isEqualTo(DEFAULT_MODO);
    }

    @Test
    void createOrdenWithExistingId() throws Exception {
        // Create the Orden with an existing ID
        orden.setId(1L);
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        int databaseSizeBeforeCreate = ordenRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Orden in the database
        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkClienteIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordenRepository.findAll().collectList().block().size();
        // set the field null
        orden.setCliente(null);

        // Create the Orden, which fails.
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAccionIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordenRepository.findAll().collectList().block().size();
        // set the field null
        orden.setAccionId(null);

        // Create the Orden, which fails.
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAccionIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordenRepository.findAll().collectList().block().size();
        // set the field null
        orden.setAccion(null);

        // Create the Orden, which fails.
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkOperacionIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordenRepository.findAll().collectList().block().size();
        // set the field null
        orden.setOperacion(null);

        // Create the Orden, which fails.
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPrecioIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordenRepository.findAll().collectList().block().size();
        // set the field null
        orden.setPrecio(null);

        // Create the Orden, which fails.
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCantidadIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordenRepository.findAll().collectList().block().size();
        // set the field null
        orden.setCantidad(null);

        // Create the Orden, which fails.
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkFechaOperacionIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordenRepository.findAll().collectList().block().size();
        // set the field null
        orden.setFechaOperacion(null);

        // Create the Orden, which fails.
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkModoIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordenRepository.findAll().collectList().block().size();
        // set the field null
        orden.setModo(null);

        // Create the Orden, which fails.
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllOrdensAsStream() {
        // Initialize the database
        ordenRepository.save(orden).block();

        List<Orden> ordenList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(OrdenDTO.class)
            .getResponseBody()
            .map(ordenMapper::toEntity)
            .filter(orden::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(ordenList).isNotNull();
        assertThat(ordenList).hasSize(1);
        Orden testOrden = ordenList.get(0);
        assertThat(testOrden.getCliente()).isEqualTo(DEFAULT_CLIENTE);
        assertThat(testOrden.getAccionId()).isEqualTo(DEFAULT_ACCION_ID);
        assertThat(testOrden.getAccion()).isEqualTo(DEFAULT_ACCION);
        assertThat(testOrden.getOperacion()).isEqualTo(DEFAULT_OPERACION);
        assertThat(testOrden.getPrecio()).isEqualTo(DEFAULT_PRECIO);
        assertThat(testOrden.getCantidad()).isEqualTo(DEFAULT_CANTIDAD);
        assertThat(testOrden.getFechaOperacion()).isEqualTo(DEFAULT_FECHA_OPERACION);
        assertThat(testOrden.getModo()).isEqualTo(DEFAULT_MODO);
    }

    @Test
    void getAllOrdens() {
        // Initialize the database
        ordenRepository.save(orden).block();

        // Get all the ordenList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(orden.getId().intValue()))
            .jsonPath("$.[*].cliente")
            .value(hasItem(DEFAULT_CLIENTE))
            .jsonPath("$.[*].accionId")
            .value(hasItem(DEFAULT_ACCION_ID))
            .jsonPath("$.[*].accion")
            .value(hasItem(DEFAULT_ACCION))
            .jsonPath("$.[*].operacion")
            .value(hasItem(DEFAULT_OPERACION))
            .jsonPath("$.[*].precio")
            .value(hasItem(DEFAULT_PRECIO.doubleValue()))
            .jsonPath("$.[*].cantidad")
            .value(hasItem(DEFAULT_CANTIDAD))
            .jsonPath("$.[*].fechaOperacion")
            .value(hasItem(DEFAULT_FECHA_OPERACION.toString()))
            .jsonPath("$.[*].modo")
            .value(hasItem(DEFAULT_MODO));
    }

    @Test
    void getOrden() {
        // Initialize the database
        ordenRepository.save(orden).block();

        // Get the orden
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, orden.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(orden.getId().intValue()))
            .jsonPath("$.cliente")
            .value(is(DEFAULT_CLIENTE))
            .jsonPath("$.accionId")
            .value(is(DEFAULT_ACCION_ID))
            .jsonPath("$.accion")
            .value(is(DEFAULT_ACCION))
            .jsonPath("$.operacion")
            .value(is(DEFAULT_OPERACION))
            .jsonPath("$.precio")
            .value(is(DEFAULT_PRECIO.doubleValue()))
            .jsonPath("$.cantidad")
            .value(is(DEFAULT_CANTIDAD))
            .jsonPath("$.fechaOperacion")
            .value(is(DEFAULT_FECHA_OPERACION.toString()))
            .jsonPath("$.modo")
            .value(is(DEFAULT_MODO));
    }

    @Test
    void getNonExistingOrden() {
        // Get the orden
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOrden() throws Exception {
        // Initialize the database
        ordenRepository.save(orden).block();

        int databaseSizeBeforeUpdate = ordenRepository.findAll().collectList().block().size();

        // Update the orden
        Orden updatedOrden = ordenRepository.findById(orden.getId()).block();
        updatedOrden
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .precio(UPDATED_PRECIO)
            .cantidad(UPDATED_CANTIDAD)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO);
        OrdenDTO ordenDTO = ordenMapper.toDto(updatedOrden);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ordenDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Orden in the database
        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeUpdate);
        Orden testOrden = ordenList.get(ordenList.size() - 1);
        assertThat(testOrden.getCliente()).isEqualTo(UPDATED_CLIENTE);
        assertThat(testOrden.getAccionId()).isEqualTo(UPDATED_ACCION_ID);
        assertThat(testOrden.getAccion()).isEqualTo(UPDATED_ACCION);
        assertThat(testOrden.getOperacion()).isEqualTo(UPDATED_OPERACION);
        assertThat(testOrden.getPrecio()).isEqualTo(UPDATED_PRECIO);
        assertThat(testOrden.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testOrden.getFechaOperacion()).isEqualTo(UPDATED_FECHA_OPERACION);
        assertThat(testOrden.getModo()).isEqualTo(UPDATED_MODO);
    }

    @Test
    void putNonExistingOrden() throws Exception {
        int databaseSizeBeforeUpdate = ordenRepository.findAll().collectList().block().size();
        orden.setId(count.incrementAndGet());

        // Create the Orden
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ordenDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Orden in the database
        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrden() throws Exception {
        int databaseSizeBeforeUpdate = ordenRepository.findAll().collectList().block().size();
        orden.setId(count.incrementAndGet());

        // Create the Orden
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Orden in the database
        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrden() throws Exception {
        int databaseSizeBeforeUpdate = ordenRepository.findAll().collectList().block().size();
        orden.setId(count.incrementAndGet());

        // Create the Orden
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Orden in the database
        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrdenWithPatch() throws Exception {
        // Initialize the database
        ordenRepository.save(orden).block();

        int databaseSizeBeforeUpdate = ordenRepository.findAll().collectList().block().size();

        // Update the orden using partial update
        Orden partialUpdatedOrden = new Orden();
        partialUpdatedOrden.setId(orden.getId());

        partialUpdatedOrden.operacion(UPDATED_OPERACION).precio(UPDATED_PRECIO).cantidad(UPDATED_CANTIDAD).modo(UPDATED_MODO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrden.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrden))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Orden in the database
        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeUpdate);
        Orden testOrden = ordenList.get(ordenList.size() - 1);
        assertThat(testOrden.getCliente()).isEqualTo(DEFAULT_CLIENTE);
        assertThat(testOrden.getAccionId()).isEqualTo(DEFAULT_ACCION_ID);
        assertThat(testOrden.getAccion()).isEqualTo(DEFAULT_ACCION);
        assertThat(testOrden.getOperacion()).isEqualTo(UPDATED_OPERACION);
        assertThat(testOrden.getPrecio()).isEqualTo(UPDATED_PRECIO);
        assertThat(testOrden.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testOrden.getFechaOperacion()).isEqualTo(DEFAULT_FECHA_OPERACION);
        assertThat(testOrden.getModo()).isEqualTo(UPDATED_MODO);
    }

    @Test
    void fullUpdateOrdenWithPatch() throws Exception {
        // Initialize the database
        ordenRepository.save(orden).block();

        int databaseSizeBeforeUpdate = ordenRepository.findAll().collectList().block().size();

        // Update the orden using partial update
        Orden partialUpdatedOrden = new Orden();
        partialUpdatedOrden.setId(orden.getId());

        partialUpdatedOrden
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .precio(UPDATED_PRECIO)
            .cantidad(UPDATED_CANTIDAD)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrden.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrden))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Orden in the database
        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeUpdate);
        Orden testOrden = ordenList.get(ordenList.size() - 1);
        assertThat(testOrden.getCliente()).isEqualTo(UPDATED_CLIENTE);
        assertThat(testOrden.getAccionId()).isEqualTo(UPDATED_ACCION_ID);
        assertThat(testOrden.getAccion()).isEqualTo(UPDATED_ACCION);
        assertThat(testOrden.getOperacion()).isEqualTo(UPDATED_OPERACION);
        assertThat(testOrden.getPrecio()).isEqualTo(UPDATED_PRECIO);
        assertThat(testOrden.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testOrden.getFechaOperacion()).isEqualTo(UPDATED_FECHA_OPERACION);
        assertThat(testOrden.getModo()).isEqualTo(UPDATED_MODO);
    }

    @Test
    void patchNonExistingOrden() throws Exception {
        int databaseSizeBeforeUpdate = ordenRepository.findAll().collectList().block().size();
        orden.setId(count.incrementAndGet());

        // Create the Orden
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, ordenDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Orden in the database
        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrden() throws Exception {
        int databaseSizeBeforeUpdate = ordenRepository.findAll().collectList().block().size();
        orden.setId(count.incrementAndGet());

        // Create the Orden
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Orden in the database
        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrden() throws Exception {
        int databaseSizeBeforeUpdate = ordenRepository.findAll().collectList().block().size();
        orden.setId(count.incrementAndGet());

        // Create the Orden
        OrdenDTO ordenDTO = ordenMapper.toDto(orden);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ordenDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Orden in the database
        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrden() {
        // Initialize the database
        ordenRepository.save(orden).block();

        int databaseSizeBeforeDelete = ordenRepository.findAll().collectList().block().size();

        // Delete the orden
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, orden.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Orden> ordenList = ordenRepository.findAll().collectList().block();
        assertThat(ordenList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
