package one.genchev.bonbon.repository;

import one.genchev.bonbon.domain.Listing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Listing entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ListingRepository extends R2dbcRepository<Listing, Long>, ListingRepositoryInternal {
    Flux<Listing> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Listing> findAll();

    @Override
    Mono<Listing> findById(Long id);

    @Override
    <S extends Listing> Mono<S> save(S entity);
}

interface ListingRepositoryInternal {
    <S extends Listing> Mono<S> insert(S entity);
    <S extends Listing> Mono<S> save(S entity);
    Mono<Integer> update(Listing entity);

    Flux<Listing> findAll();
    Mono<Listing> findById(Long id);
    Flux<Listing> findAllBy(Pageable pageable);
    Flux<Listing> findAllBy(Pageable pageable, Criteria criteria);
}
