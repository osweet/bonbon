package one.genchev.bonbon.service;

import one.genchev.bonbon.domain.Listing;
import one.genchev.bonbon.repository.ListingRepository;
import one.genchev.bonbon.service.dto.ListingDTO;
import one.genchev.bonbon.service.mapper.ListingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Listing}.
 */
@Service
@Transactional
public class ListingService {

    private final Logger log = LoggerFactory.getLogger(ListingService.class);

    private final ListingRepository listingRepository;

    private final ListingMapper listingMapper;

    public ListingService(ListingRepository listingRepository, ListingMapper listingMapper) {
        this.listingRepository = listingRepository;
        this.listingMapper = listingMapper;
    }

    /**
     * Save a listing.
     *
     * @param listingDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ListingDTO> save(ListingDTO listingDTO) {
        log.debug("Request to save Listing : {}", listingDTO);
        return listingRepository.save(listingMapper.toEntity(listingDTO)).map(listingMapper::toDto);
    }

    /**
     * Partially update a listing.
     *
     * @param listingDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ListingDTO> partialUpdate(ListingDTO listingDTO) {
        log.debug("Request to partially update Listing : {}", listingDTO);

        return listingRepository
            .findById(listingDTO.getId())
            .map(existingListing -> {
                listingMapper.partialUpdate(existingListing, listingDTO);

                return existingListing;
            })
            .flatMap(listingRepository::save)
            .map(listingMapper::toDto);
    }

    /**
     * Get all the listings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ListingDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Listings");
        return listingRepository.findAllBy(pageable).map(listingMapper::toDto);
    }

    /**
     * Returns the number of listings available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return listingRepository.count();
    }

    /**
     * Get one listing by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ListingDTO> findOne(Long id) {
        log.debug("Request to get Listing : {}", id);
        return listingRepository.findById(id).map(listingMapper::toDto);
    }

    /**
     * Delete the listing by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Listing : {}", id);
        return listingRepository.deleteById(id);
    }
}
