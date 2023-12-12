package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.domain.Reporte;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Reporte entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReporteRepository extends ReactiveCrudRepository<Reporte, Long>, ReporteRepositoryInternal {

    @Override
    <S extends Reporte> Flux<S> saveAll(Iterable<S> entities);
    @Override
    <S extends Reporte> Mono<S> save(S entity);

    @Override
    Flux<Reporte> findAll();

    @Override
    Mono<Reporte> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ReporteRepositoryInternal {

    <S extends Reporte> Flux<S> saveAll(Iterable<S> entities);
    <S extends Reporte> Mono<S> save(S entity);

    Flux<Reporte> findAllBy(Pageable pageable);

    Flux<Reporte> findAll();

    Mono<Reporte> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Reporte> findAllBy(Pageable pageable, Criteria criteria);

}
