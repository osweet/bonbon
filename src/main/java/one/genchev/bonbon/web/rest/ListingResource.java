package one.genchev.bonbon.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import one.genchev.bonbon.repository.ListingRepository;
import one.genchev.bonbon.service.ListingService;
import one.genchev.bonbon.service.dto.ListingDTO;
import one.genchev.bonbon.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link one.genchev.bonbon.domain.Listing}.
 */
@RestController
@RequestMapping("/api")
public class ListingResource {

    private final Logger log = LoggerFactory.getLogger(ListingResource.class);

    private static final String ENTITY_NAME = "listing";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ListingService listingService;

    private final ListingRepository listingRepository;

    public ListingResource(ListingService listingService, ListingRepository listingRepository) {
        this.listingService = listingService;
        this.listingRepository = listingRepository;
    }

    /**
     * {@code POST  /listings} : Create a new listing.
     *
     * @param listingDTO the listingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new listingDTO, or with status {@code 400 (Bad Request)} if the listing has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/listings")
    public Mono<ResponseEntity<ListingDTO>> createListing(@Valid @RequestBody ListingDTO listingDTO) throws URISyntaxException {
        log.debug("REST request to save Listing : {}", listingDTO);
        if (listingDTO.getId() != null) {
            throw new BadRequestAlertException("A new listing cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return listingService
            .save(listingDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/listings/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /listings/:id} : Updates an existing listing.
     *
     * @param id the id of the listingDTO to save.
     * @param listingDTO the listingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated listingDTO,
     * or with status {@code 400 (Bad Request)} if the listingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the listingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/listings/{id}")
    public Mono<ResponseEntity<ListingDTO>> updateListing(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ListingDTO listingDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Listing : {}, {}", id, listingDTO);
        if (listingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, listingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return listingRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return listingService
                    .save(listingDTO)
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
     * {@code PATCH  /listings/:id} : Partial updates given fields of an existing listing, field will ignore if it is null
     *
     * @param id the id of the listingDTO to save.
     * @param listingDTO the listingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated listingDTO,
     * or with status {@code 400 (Bad Request)} if the listingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the listingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the listingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/listings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ListingDTO>> partialUpdateListing(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ListingDTO listingDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Listing partially : {}, {}", id, listingDTO);
        if (listingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, listingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return listingRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ListingDTO> result = listingService.partialUpdate(listingDTO);

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
     * {@code GET  /listings} : get all the listings.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of listings in body.
     */
    @GetMapping("/listings")
    public Mono<ResponseEntity<List<ListingDTO>>> getAllListings(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Listings");
        return listingService
            .countAll()
            .zipWith(listingService.findAll(pageable).collectList())
            .map(countWithEntities -> {
                return ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2());
            });
    }

    /**
     * {@code GET  /listings/:id} : get the "id" listing.
     *
     * @param id the id of the listingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the listingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/listings/{id}")
    public Mono<ResponseEntity<ListingDTO>> getListing(@PathVariable Long id) {
        log.debug("REST request to get Listing : {}", id);
        Mono<ListingDTO> listingDTO = listingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(listingDTO);
    }

    /**
     * {@code DELETE  /listings/:id} : delete the "id" listing.
     *
     * @param id the id of the listingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/listings/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteListing(@PathVariable Long id) {
        log.debug("REST request to delete Listing : {}", id);
        return listingService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
