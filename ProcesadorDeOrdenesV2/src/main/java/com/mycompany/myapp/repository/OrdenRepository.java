package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Orden;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Orden entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdenRepository extends ReactiveCrudRepository<Orden, Long>, OrdenRepositoryInternal {

    @Override
    <S extends Orden> Flux<S> saveAll(Iterable<S> entities);

    @Override
    <S extends Orden> Mono<S> save(S entity);

    @Override
    Flux<Orden> findAll();

    @Override
    Mono<Orden> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface OrdenRepositoryInternal {

    <S extends Orden> Flux<S> saveAll(Iterable<S> entities);

    <S extends Orden> Mono<S> save(S entity);

    Flux<Orden> findAllBy(Pageable pageable);

    Flux<Orden> findAll();

    Mono<Orden> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Orden> findAllBy(Pageable pageable, Criteria criteria);

}
