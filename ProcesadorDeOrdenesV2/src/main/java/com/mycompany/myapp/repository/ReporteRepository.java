package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.domain.Reporte;
import io.r2dbc.spi.Parameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

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

    @Query("SELECT * FROM reporte  WHERE (:cliente IS NULL OR cliente=:cliente) AND (:accion IS NULL OR accion_Id=:accion) AND (:fechaInicio IS NULL OR fecha_operacion BETWEEN :fechaInicio AND :fechaFinal)")
    Flux<Reporte> findAllQuery(@Param("cliente") Integer cliente, @Param("accion") Integer accion, @Param("fechaInicio")LocalDateTime fechaInicio, @Param("fechaFinal")LocalDateTime fechaFinal);

    @Query("SELECT * FROM reporte WHERE operacion_exitosa=false")
    Flux<Reporte> findAllQueryNoProc();


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
