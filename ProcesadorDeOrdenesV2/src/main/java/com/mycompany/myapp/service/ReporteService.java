package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Reporte;
import com.mycompany.myapp.repository.ReporteRepository;
import com.mycompany.myapp.service.dto.ReporteDTO;
import com.mycompany.myapp.service.mapper.ReporteMapper;
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
 * Service Implementation for managing {@link Reporte}.
 */
@Service
@Transactional
public class ReporteService {

    private final Logger log = LoggerFactory.getLogger(ReporteService.class);

    private final ReporteRepository reporteRepository;

    private final ReporteMapper reporteMapper;

    public ReporteService(ReporteRepository reporteRepository, ReporteMapper reporteMapper) {
        this.reporteRepository = reporteRepository;
        this.reporteMapper = reporteMapper;
    }

    /**
     * Save a reporte.
     *
     * @param reporteDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ReporteDTO> save(ReporteDTO reporteDTO) {
        log.debug("Request to save Reporte : {}", reporteDTO);
        return reporteRepository.save(reporteMapper.toEntity(reporteDTO)).map(reporteMapper::toDto);
    }

    public Mono<Void> saveAll(List<ReporteDTO> reporteDTO) {
        log.debug("Request to saveAll Reporte : {}", reporteDTO);
        return reporteRepository.saveAll(reporteMapper.toEntity(reporteDTO)).then();
    }

    /**
     * Update a reporte.
     *
     * @param reporteDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ReporteDTO> update(ReporteDTO reporteDTO) {
        log.debug("Request to update Reporte : {}", reporteDTO);
        return reporteRepository.save(reporteMapper.toEntity(reporteDTO)).map(reporteMapper::toDto);
    }

    /**
     * Partially update a reporte.
     *
     * @param reporteDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ReporteDTO> partialUpdate(ReporteDTO reporteDTO) {
        log.debug("Request to partially update Reporte : {}", reporteDTO);

        return reporteRepository
            .findById(reporteDTO.getId())
            .map(existingReporte -> {
                reporteMapper.partialUpdate(existingReporte, reporteDTO);

                return existingReporte;
            })
            .flatMap(reporteRepository::save)
            .map(reporteMapper::toDto);
    }

    /**
     * Get all the reportes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ReporteDTO> findAll() {
        log.debug("Request to get all Reportes");
        return reporteRepository.findAll().map(reporteMapper::toDto);
    }

    /**
     * Returns the number of reportes available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return reporteRepository.count();
    }

    /**
     * Get one reporte by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ReporteDTO> findOne(Long id) {
        log.debug("Request to get Reporte : {}", id);
        return reporteRepository.findById(id).map(reporteMapper::toDto);
    }

    /**
     * Delete the reporte by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Reporte : {}", id);
        return reporteRepository.deleteById(id);
    }

}
