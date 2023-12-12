package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Reporte;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ReporteRepository;
import com.mycompany.myapp.service.dto.ReporteDTO;
import com.mycompany.myapp.service.mapper.ReporteMapper;

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
 * Integration tests for the {@link ReporteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ReporteResourceIT {

    private static final Integer DEFAULT_CLIENTE = 1;
    private static final Integer UPDATED_CLIENTE = 2;

    private static final Integer DEFAULT_ACCION_ID = 1;
    private static final Integer UPDATED_ACCION_ID = 2;

    private static final String DEFAULT_ACCION = "AAAAAAAAAA";
    private static final String UPDATED_ACCION = "BBBBBBBBBB";

    private static final String DEFAULT_OPERACION = "AAAAAAAAAA";
    private static final String UPDATED_OPERACION = "BBBBBBBBBB";

    private static final Integer DEFAULT_CANTIDAD = 1;
    private static final Integer UPDATED_CANTIDAD = 2;

    private static final Float DEFAULT_PRECIO = 1F;
    private static final Float UPDATED_PRECIO = 2F;

    private static final LocalDateTime DEFAULT_FECHA_OPERACION = LocalDateTime.ofEpochSecond(0L,0, ZoneOffset.MIN);
    private static final LocalDateTime UPDATED_FECHA_OPERACION = LocalDateTime.now();

    private static final String DEFAULT_MODO = "AAAAAAAAAA";
    private static final String UPDATED_MODO = "BBBBBBBBBB";

    private static final Boolean DEFAULT_OPERACION_EXITOSA = false;
    private static final Boolean UPDATED_OPERACION_EXITOSA = true;

    private static final String DEFAULT_OPERACION_OBSERVACIONES = "AAAAAAAAAA";
    private static final String UPDATED_OPERACION_OBSERVACIONES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/reportes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private ReporteMapper reporteMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Reporte reporte;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reporte createEntity(EntityManager em) {
        Reporte reporte = new Reporte()
            .cliente(DEFAULT_CLIENTE)
            .accionId(DEFAULT_ACCION_ID)
            .accion(DEFAULT_ACCION)
            .operacion(DEFAULT_OPERACION)
            .cantidad(DEFAULT_CANTIDAD)
            .precio(DEFAULT_PRECIO)
            .fechaOperacion(DEFAULT_FECHA_OPERACION)
            .modo(DEFAULT_MODO)
            .operacionExitosa(DEFAULT_OPERACION_EXITOSA)
            .operacionObservaciones(DEFAULT_OPERACION_OBSERVACIONES);
        return reporte;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reporte createUpdatedEntity(EntityManager em) {
        Reporte reporte = new Reporte()
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .cantidad(UPDATED_CANTIDAD)
            .precio(UPDATED_PRECIO)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO)
            .operacionExitosa(UPDATED_OPERACION_EXITOSA)
            .operacionObservaciones(UPDATED_OPERACION_OBSERVACIONES);
        return reporte;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Reporte.class).block();
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
        reporte = createEntity(em);
    }

    @Test
    void createReporte() throws Exception {
        int databaseSizeBeforeCreate = reporteRepository.findAll().collectList().block().size();
        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeCreate + 1);
        Reporte testReporte = reporteList.get(reporteList.size() - 1);
        assertThat(testReporte.getCliente()).isEqualTo(DEFAULT_CLIENTE);
        assertThat(testReporte.getAccionId()).isEqualTo(DEFAULT_ACCION_ID);
        assertThat(testReporte.getAccion()).isEqualTo(DEFAULT_ACCION);
        assertThat(testReporte.getOperacion()).isEqualTo(DEFAULT_OPERACION);
        assertThat(testReporte.getCantidad()).isEqualTo(DEFAULT_CANTIDAD);
        assertThat(testReporte.getPrecio()).isEqualTo(DEFAULT_PRECIO);
        assertThat(testReporte.getFechaOperacion()).isEqualTo(DEFAULT_FECHA_OPERACION);
        assertThat(testReporte.getModo()).isEqualTo(DEFAULT_MODO);
        assertThat(testReporte.getOperacionExitosa()).isEqualTo(DEFAULT_OPERACION_EXITOSA);
        assertThat(testReporte.getOperacionObservaciones()).isEqualTo(DEFAULT_OPERACION_OBSERVACIONES);
    }

    @Test
    void createReporteWithExistingId() throws Exception {
        // Create the Reporte with an existing ID
        reporte.setId(1L);
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        int databaseSizeBeforeCreate = reporteRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkClienteIsRequired() throws Exception {
        int databaseSizeBeforeTest = reporteRepository.findAll().collectList().block().size();
        // set the field null
        reporte.setCliente(null);

        // Create the Reporte, which fails.
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAccionIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = reporteRepository.findAll().collectList().block().size();
        // set the field null
        reporte.setAccionId(null);

        // Create the Reporte, which fails.
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAccionIsRequired() throws Exception {
        int databaseSizeBeforeTest = reporteRepository.findAll().collectList().block().size();
        // set the field null
        reporte.setAccion(null);

        // Create the Reporte, which fails.
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkOperacionIsRequired() throws Exception {
        int databaseSizeBeforeTest = reporteRepository.findAll().collectList().block().size();
        // set the field null
        reporte.setOperacion(null);

        // Create the Reporte, which fails.
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCantidadIsRequired() throws Exception {
        int databaseSizeBeforeTest = reporteRepository.findAll().collectList().block().size();
        // set the field null
        reporte.setCantidad(null);

        // Create the Reporte, which fails.
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPrecioIsRequired() throws Exception {
        int databaseSizeBeforeTest = reporteRepository.findAll().collectList().block().size();
        // set the field null
        reporte.setPrecio(null);

        // Create the Reporte, which fails.
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkFechaOperacionIsRequired() throws Exception {
        int databaseSizeBeforeTest = reporteRepository.findAll().collectList().block().size();
        // set the field null
        reporte.setFechaOperacion(null);

        // Create the Reporte, which fails.
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkModoIsRequired() throws Exception {
        int databaseSizeBeforeTest = reporteRepository.findAll().collectList().block().size();
        // set the field null
        reporte.setModo(null);

        // Create the Reporte, which fails.
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkOperacionExitosaIsRequired() throws Exception {
        int databaseSizeBeforeTest = reporteRepository.findAll().collectList().block().size();
        // set the field null
        reporte.setOperacionExitosa(null);

        // Create the Reporte, which fails.
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkOperacionObservacionesIsRequired() throws Exception {
        int databaseSizeBeforeTest = reporteRepository.findAll().collectList().block().size();
        // set the field null
        reporte.setOperacionObservaciones(null);

        // Create the Reporte, which fails.
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllReportesAsStream() {
        // Initialize the database
        reporteRepository.save(reporte).block();

        List<Reporte> reporteList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ReporteDTO.class)
            .getResponseBody()
            .map(reporteMapper::toEntity)
            .filter(reporte::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(reporteList).isNotNull();
        assertThat(reporteList).hasSize(1);
        Reporte testReporte = reporteList.get(0);
        assertThat(testReporte.getCliente()).isEqualTo(DEFAULT_CLIENTE);
        assertThat(testReporte.getAccionId()).isEqualTo(DEFAULT_ACCION_ID);
        assertThat(testReporte.getAccion()).isEqualTo(DEFAULT_ACCION);
        assertThat(testReporte.getOperacion()).isEqualTo(DEFAULT_OPERACION);
        assertThat(testReporte.getCantidad()).isEqualTo(DEFAULT_CANTIDAD);
        assertThat(testReporte.getPrecio()).isEqualTo(DEFAULT_PRECIO);
        assertThat(testReporte.getFechaOperacion()).isEqualTo(DEFAULT_FECHA_OPERACION);
        assertThat(testReporte.getModo()).isEqualTo(DEFAULT_MODO);
        assertThat(testReporte.getOperacionExitosa()).isEqualTo(DEFAULT_OPERACION_EXITOSA);
        assertThat(testReporte.getOperacionObservaciones()).isEqualTo(DEFAULT_OPERACION_OBSERVACIONES);
    }

    @Test
    void getAllReportes() {
        // Initialize the database
        reporteRepository.save(reporte).block();

        // Get all the reporteList
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
            .value(hasItem(reporte.getId().intValue()))
            .jsonPath("$.[*].cliente")
            .value(hasItem(DEFAULT_CLIENTE))
            .jsonPath("$.[*].accionId")
            .value(hasItem(DEFAULT_ACCION_ID))
            .jsonPath("$.[*].accion")
            .value(hasItem(DEFAULT_ACCION))
            .jsonPath("$.[*].operacion")
            .value(hasItem(DEFAULT_OPERACION))
            .jsonPath("$.[*].cantidad")
            .value(hasItem(DEFAULT_CANTIDAD))
            .jsonPath("$.[*].precio")
            .value(hasItem(DEFAULT_PRECIO.doubleValue()))
            .jsonPath("$.[*].fechaOperacion")
            .value(hasItem(DEFAULT_FECHA_OPERACION.toString()))
            .jsonPath("$.[*].modo")
            .value(hasItem(DEFAULT_MODO))
            .jsonPath("$.[*].operacionExitosa")
            .value(hasItem(DEFAULT_OPERACION_EXITOSA.booleanValue()))
            .jsonPath("$.[*].operacionObservaciones")
            .value(hasItem(DEFAULT_OPERACION_OBSERVACIONES));
    }

    @Test
    void getReporte() {
        // Initialize the database
        reporteRepository.save(reporte).block();

        // Get the reporte
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, reporte.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(reporte.getId().intValue()))
            .jsonPath("$.cliente")
            .value(is(DEFAULT_CLIENTE))
            .jsonPath("$.accionId")
            .value(is(DEFAULT_ACCION_ID))
            .jsonPath("$.accion")
            .value(is(DEFAULT_ACCION))
            .jsonPath("$.operacion")
            .value(is(DEFAULT_OPERACION))
            .jsonPath("$.cantidad")
            .value(is(DEFAULT_CANTIDAD))
            .jsonPath("$.precio")
            .value(is(DEFAULT_PRECIO.doubleValue()))
            .jsonPath("$.fechaOperacion")
            .value(is(DEFAULT_FECHA_OPERACION.toString()))
            .jsonPath("$.modo")
            .value(is(DEFAULT_MODO))
            .jsonPath("$.operacionExitosa")
            .value(is(DEFAULT_OPERACION_EXITOSA.booleanValue()))
            .jsonPath("$.operacionObservaciones")
            .value(is(DEFAULT_OPERACION_OBSERVACIONES));
    }

    @Test
    void getNonExistingReporte() {
        // Get the reporte
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingReporte() throws Exception {
        // Initialize the database
        reporteRepository.save(reporte).block();

        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();

        // Update the reporte
        Reporte updatedReporte = reporteRepository.findById(reporte.getId()).block();
        updatedReporte
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .cantidad(UPDATED_CANTIDAD)
            .precio(UPDATED_PRECIO)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO)
            .operacionExitosa(UPDATED_OPERACION_EXITOSA)
            .operacionObservaciones(UPDATED_OPERACION_OBSERVACIONES);
        ReporteDTO reporteDTO = reporteMapper.toDto(updatedReporte);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, reporteDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
        Reporte testReporte = reporteList.get(reporteList.size() - 1);
        assertThat(testReporte.getCliente()).isEqualTo(UPDATED_CLIENTE);
        assertThat(testReporte.getAccionId()).isEqualTo(UPDATED_ACCION_ID);
        assertThat(testReporte.getAccion()).isEqualTo(UPDATED_ACCION);
        assertThat(testReporte.getOperacion()).isEqualTo(UPDATED_OPERACION);
        assertThat(testReporte.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testReporte.getPrecio()).isEqualTo(UPDATED_PRECIO);
        assertThat(testReporte.getFechaOperacion()).isEqualTo(UPDATED_FECHA_OPERACION);
        assertThat(testReporte.getModo()).isEqualTo(UPDATED_MODO);
        assertThat(testReporte.getOperacionExitosa()).isEqualTo(UPDATED_OPERACION_EXITOSA);
        assertThat(testReporte.getOperacionObservaciones()).isEqualTo(UPDATED_OPERACION_OBSERVACIONES);
    }

    @Test
    void putNonExistingReporte() throws Exception {
        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        reporte.setId(count.incrementAndGet());

        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, reporteDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchReporte() throws Exception {
        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        reporte.setId(count.incrementAndGet());

        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamReporte() throws Exception {
        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        reporte.setId(count.incrementAndGet());

        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateReporteWithPatch() throws Exception {
        // Initialize the database
        reporteRepository.save(reporte).block();

        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();

        // Update the reporte using partial update
        Reporte partialUpdatedReporte = new Reporte();
        partialUpdatedReporte.setId(reporte.getId());

        partialUpdatedReporte.cliente(UPDATED_CLIENTE).operacionExitosa(UPDATED_OPERACION_EXITOSA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReporte.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReporte))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
        Reporte testReporte = reporteList.get(reporteList.size() - 1);
        assertThat(testReporte.getCliente()).isEqualTo(DEFAULT_CLIENTE);
        assertThat(testReporte.getAccionId()).isEqualTo(DEFAULT_ACCION_ID);
        assertThat(testReporte.getAccion()).isEqualTo(DEFAULT_ACCION);
        assertThat(testReporte.getOperacion()).isEqualTo(DEFAULT_OPERACION);
        assertThat(testReporte.getCantidad()).isEqualTo(DEFAULT_CANTIDAD);
        assertThat(testReporte.getPrecio()).isEqualTo(DEFAULT_PRECIO);
        assertThat(testReporte.getFechaOperacion()).isEqualTo(DEFAULT_FECHA_OPERACION);
        assertThat(testReporte.getModo()).isEqualTo(DEFAULT_MODO);
        assertThat(testReporte.getOperacionExitosa()).isEqualTo(UPDATED_OPERACION_EXITOSA);
        assertThat(testReporte.getOperacionObservaciones()).isEqualTo(DEFAULT_OPERACION_OBSERVACIONES);
    }

    @Test
    void fullUpdateReporteWithPatch() throws Exception {
        // Initialize the database
        reporteRepository.save(reporte).block();

        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();

        // Update the reporte using partial update
        Reporte partialUpdatedReporte = new Reporte();
        partialUpdatedReporte.setId(reporte.getId());

        partialUpdatedReporte
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .cantidad(UPDATED_CANTIDAD)
            .precio(UPDATED_PRECIO)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO)
            .operacionExitosa(UPDATED_OPERACION_EXITOSA)
            .operacionObservaciones(UPDATED_OPERACION_OBSERVACIONES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReporte.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReporte))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
        Reporte testReporte = reporteList.get(reporteList.size() - 1);
        assertThat(testReporte.getCliente()).isEqualTo(UPDATED_CLIENTE);
        assertThat(testReporte.getAccionId()).isEqualTo(UPDATED_ACCION_ID);
        assertThat(testReporte.getAccion()).isEqualTo(UPDATED_ACCION);
        assertThat(testReporte.getOperacion()).isEqualTo(UPDATED_OPERACION);
        assertThat(testReporte.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testReporte.getPrecio()).isEqualTo(UPDATED_PRECIO);
        assertThat(testReporte.getFechaOperacion()).isEqualTo(UPDATED_FECHA_OPERACION);
        assertThat(testReporte.getModo()).isEqualTo(UPDATED_MODO);
        assertThat(testReporte.getOperacionExitosa()).isEqualTo(UPDATED_OPERACION_EXITOSA);
        assertThat(testReporte.getOperacionObservaciones()).isEqualTo(UPDATED_OPERACION_OBSERVACIONES);
    }

    @Test
    void patchNonExistingReporte() throws Exception {
        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        reporte.setId(count.incrementAndGet());

        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, reporteDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchReporte() throws Exception {
        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        reporte.setId(count.incrementAndGet());

        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamReporte() throws Exception {
        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        reporte.setId(count.incrementAndGet());

        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteReporte() {
        // Initialize the database
        reporteRepository.save(reporte).block();

        int databaseSizeBeforeDelete = reporteRepository.findAll().collectList().block().size();

        // Delete the reporte
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, reporte.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
