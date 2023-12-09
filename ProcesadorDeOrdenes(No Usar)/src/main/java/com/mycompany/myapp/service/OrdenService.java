package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.repository.OrdenRepository;
import com.mycompany.myapp.service.dto.OrdenDTO;
import com.mycompany.myapp.service.mapper.OrdenMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Orden}.
 */
@Service
@Transactional
public class OrdenService {

    private final Logger log = LoggerFactory.getLogger(OrdenService.class);

    private final OrdenRepository ordenRepository;

    private final OrdenMapper ordenMapper;

    public OrdenService(OrdenRepository ordenRepository, OrdenMapper ordenMapper) {
        this.ordenRepository = ordenRepository;
        this.ordenMapper = ordenMapper;
    }

    /**
     * Save a orden.
     *
     * @param ordenDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrdenDTO> save(OrdenDTO ordenDTO) {
        log.debug("Request to save Orden : {}", ordenDTO);
        return ordenRepository.save(ordenMapper.toEntity(ordenDTO)).map(ordenMapper::toDto);
    }

    /**
     * Update a orden.
     *
     * @param ordenDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrdenDTO> update(OrdenDTO ordenDTO) {
        log.debug("Request to update Orden : {}", ordenDTO);
        return ordenRepository.save(ordenMapper.toEntity(ordenDTO)).map(ordenMapper::toDto);
    }

    /**
     * Partially update a orden.
     *
     * @param ordenDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OrdenDTO> partialUpdate(OrdenDTO ordenDTO) {
        log.debug("Request to partially update Orden : {}", ordenDTO);

        return ordenRepository
            .findById(ordenDTO.getId())
            .map(existingOrden -> {
                ordenMapper.partialUpdate(existingOrden, ordenDTO);

                return existingOrden;
            })
            .flatMap(ordenRepository::save)
            .map(ordenMapper::toDto);
    }

    /**
     * Get all the ordens.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrdenDTO> findAll() {
        log.debug("Request to get all Ordens");
        return ordenRepository.findAll().map(ordenMapper::toDto);
    }

    /**
     * Returns the number of ordens available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return ordenRepository.count();
    }

    /**
     * Get one orden by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OrdenDTO> findOne(Long id) {
        log.debug("Request to get Orden : {}", id);
        return ordenRepository.findById(id).map(ordenMapper::toDto);
    }

    /**
     * Delete the orden by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Orden : {}", id);
        return ordenRepository.deleteById(id);
    }
}
