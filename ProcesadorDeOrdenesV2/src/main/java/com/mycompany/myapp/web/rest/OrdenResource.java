package com.mycompany.myapp.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.repository.OrdenRepository;
import com.mycompany.myapp.service.AnalizarOrdenService;
import com.mycompany.myapp.service.OrdenService;
import com.mycompany.myapp.service.dto.OrdenDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.result.method.annotation.AbstractNamedValueArgumentResolver;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Orden}.
 */
@RestController
@RequestMapping("/api")
public class OrdenResource {

    private final RestTemplate restTemplate;

    private final AnalizarOrdenService analizarOrdenService;

    @Value("${external.service.url}")
    private String externalServiceUrl;

    @Value("${externalBearer.token}")
    private String bearerToken;

    @Value("${localBearer.token}")
    private String bearerLocalToken;

    private final Logger log = LoggerFactory.getLogger(OrdenResource.class);

    private static final String ENTITY_NAME = "procesadorDeOrdenesV2Orden";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrdenService ordenService;

    private final OrdenRepository ordenRepository;

    public OrdenResource(OrdenService ordenService, OrdenRepository ordenRepository, RestTemplate restTemplate, AnalizarOrdenService analizarOrdenService) {
        this.ordenService = ordenService;
        this.ordenRepository = ordenRepository;
        this.restTemplate = restTemplate;
        this.analizarOrdenService = analizarOrdenService;
    }

    /**
     * {@code POST  /ordens} : Create a new orden.
     *
     * @param ordenDTO the ordenDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ordenDTO, or with status {@code 400 (Bad Request)} if the orden has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ordens")
    public Mono<ResponseEntity<OrdenDTO>> createOrden(@Valid @RequestBody OrdenDTO ordenDTO) throws URISyntaxException {
        log.debug("REST request to save Orden : {}", ordenDTO);
        if (ordenDTO.getId() != null) {
            throw new BadRequestAlertException("A new orden cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return ordenService
            .save(ordenDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/ordens/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /ordens/:id} : Updates an existing orden.
     *
     * @param id the id of the ordenDTO to save.
     * @param ordenDTO the ordenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ordenDTO,
     * or with status {@code 400 (Bad Request)} if the ordenDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ordenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ordens/{id}")
    public Mono<ResponseEntity<OrdenDTO>> updateOrden(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrdenDTO ordenDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Orden : {}, {}", id, ordenDTO);
        if (ordenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ordenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ordenRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return ordenService
                    .update(ordenDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /ordens/:id} : Partial updates given fields of an existing orden, field will ignore if it is null
     *
     * @param id the id of the ordenDTO to save.
     * @param ordenDTO the ordenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ordenDTO,
     * or with status {@code 400 (Bad Request)} if the ordenDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ordenDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ordenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ordens/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OrdenDTO>> partialUpdateOrden(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrdenDTO ordenDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Orden partially : {}, {}", id, ordenDTO);
        if (ordenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ordenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ordenRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OrdenDTO> result = ordenService.partialUpdate(ordenDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /ordens} : get all the ordens.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ordens in body.
     */
    @GetMapping("/ordens")
    public Mono<List<OrdenDTO>> getAllOrdens() {
        log.debug("REST request to get all Ordens");
        return ordenService.findAll().collectList();
    }

    /**
     * {@code GET  /ordens} : get all the ordens as a stream.
     * @return the {@link Flux} of ordens.
     */
    @GetMapping(value = "/ordens", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<OrdenDTO> getAllOrdensAsStream() {
        log.debug("REST request to get all Ordens as a stream");
        return ordenService.findAll();
    }

    /**
     * {@code GET  /ordens/:id} : get the "id" orden.
     *
     * @param id the id of the ordenDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ordenDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ordens/{id}")
    public Mono<ResponseEntity<OrdenDTO>> getOrden(@PathVariable Long id) {
        log.debug("REST request to get Orden : {}", id);
        Mono<OrdenDTO> ordenDTO = ordenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ordenDTO);
    }

    /**
     * {@code DELETE  /ordens/:id} : delete the "id" orden.
     *
     * @param id the id of the ordenDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ordens/{id}")
    public Mono<ResponseEntity<Void>> deleteOrden(@PathVariable Long id) {
        log.debug("REST request to delete Orden : {}", id);
        return ordenService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }


    // ------------------------------------------------------------------------------------------------------------------------------


    @GetMapping("/ordenes")
    public  Mono<Void> getOrdenesCatedra() {

        log.debug("Request para obtener lista de ordenes de la catedra");

        String externalEndpoint = "/api/ordenes/ordenes";

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        List<OrdenDTO> ordenDTOList;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);

        //  Get Request a la catedra para obtener la lista de ordenes sin procesar
        HttpEntity <String> requestGetEntity = new HttpEntity<>(headers);
        ResponseEntity <String> jsonData = restTemplate.exchange(
            externalServiceUrl + externalEndpoint,
            HttpMethod.GET,
            requestGetEntity,
            String.class
        );

        //  Si se obtiene la lista, almacenarla en la DB del servicio
        if (jsonData.getStatusCode() == HttpStatus.OK) {

            //  Transformar el JSON del Request a una lista de OrdenDTO
            try {
                ordenDTOList = mapper.readValue(jsonData.getBody().substring(11, jsonData.getBody().length() - 1), new TypeReference<List<OrdenDTO>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            return  ordenService.saveAll(ordenDTOList);

        } else {
            return null;
        }

    }

    @GetMapping("/analizar-ordenes")
    public String analizarOrdenes() {
        log.debug("Request para analizar las ordenes");

        analizarOrdenService.analizarOrdenes();

        return "Ok";
    }


}
