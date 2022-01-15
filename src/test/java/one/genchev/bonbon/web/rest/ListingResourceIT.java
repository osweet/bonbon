package one.genchev.bonbon.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import one.genchev.bonbon.IntegrationTest;
import one.genchev.bonbon.domain.Listing;
import one.genchev.bonbon.repository.ListingRepository;
import one.genchev.bonbon.service.EntityManager;
import one.genchev.bonbon.service.dto.ListingDTO;
import one.genchev.bonbon.service.mapper.ListingMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ListingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class ListingResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Float DEFAULT_PRICE = 1F;
    private static final Float UPDATED_PRICE = 2F;

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/listings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ListingMapper listingMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Listing listing;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Listing createEntity(EntityManager em) {
        Listing listing = new Listing()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .address(DEFAULT_ADDRESS)
            .category(DEFAULT_CATEGORY);
        return listing;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Listing createUpdatedEntity(EntityManager em) {
        Listing listing = new Listing()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .address(UPDATED_ADDRESS)
            .category(UPDATED_CATEGORY);
        return listing;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Listing.class).block();
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
        listing = createEntity(em);
    }

    @Test
    void createListing() throws Exception {
        int databaseSizeBeforeCreate = listingRepository.findAll().collectList().block().size();
        // Create the Listing
        ListingDTO listingDTO = listingMapper.toDto(listing);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(listingDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Listing in the database
        List<Listing> listingList = listingRepository.findAll().collectList().block();
        assertThat(listingList).hasSize(databaseSizeBeforeCreate + 1);
        Listing testListing = listingList.get(listingList.size() - 1);
        assertThat(testListing.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testListing.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testListing.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testListing.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testListing.getCategory()).isEqualTo(DEFAULT_CATEGORY);
    }

    @Test
    void createListingWithExistingId() throws Exception {
        // Create the Listing with an existing ID
        listing.setId(1L);
        ListingDTO listingDTO = listingMapper.toDto(listing);

        int databaseSizeBeforeCreate = listingRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(listingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Listing in the database
        List<Listing> listingList = listingRepository.findAll().collectList().block();
        assertThat(listingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllListings() {
        // Initialize the database
        listingRepository.save(listing).block();

        // Get all the listingList
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
            .value(hasItem(listing.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(DEFAULT_PRICE.doubleValue()))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].category")
            .value(hasItem(DEFAULT_CATEGORY));
    }

    @Test
    void getListing() {
        // Initialize the database
        listingRepository.save(listing).block();

        // Get the listing
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, listing.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(listing.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.price")
            .value(is(DEFAULT_PRICE.doubleValue()))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.category")
            .value(is(DEFAULT_CATEGORY));
    }

    @Test
    void getNonExistingListing() {
        // Get the listing
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewListing() throws Exception {
        // Initialize the database
        listingRepository.save(listing).block();

        int databaseSizeBeforeUpdate = listingRepository.findAll().collectList().block().size();

        // Update the listing
        Listing updatedListing = listingRepository.findById(listing.getId()).block();
        updatedListing
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .address(UPDATED_ADDRESS)
            .category(UPDATED_CATEGORY);
        ListingDTO listingDTO = listingMapper.toDto(updatedListing);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, listingDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(listingDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Listing in the database
        List<Listing> listingList = listingRepository.findAll().collectList().block();
        assertThat(listingList).hasSize(databaseSizeBeforeUpdate);
        Listing testListing = listingList.get(listingList.size() - 1);
        assertThat(testListing.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testListing.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testListing.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testListing.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testListing.getCategory()).isEqualTo(UPDATED_CATEGORY);
    }

    @Test
    void putNonExistingListing() throws Exception {
        int databaseSizeBeforeUpdate = listingRepository.findAll().collectList().block().size();
        listing.setId(count.incrementAndGet());

        // Create the Listing
        ListingDTO listingDTO = listingMapper.toDto(listing);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, listingDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(listingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Listing in the database
        List<Listing> listingList = listingRepository.findAll().collectList().block();
        assertThat(listingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchListing() throws Exception {
        int databaseSizeBeforeUpdate = listingRepository.findAll().collectList().block().size();
        listing.setId(count.incrementAndGet());

        // Create the Listing
        ListingDTO listingDTO = listingMapper.toDto(listing);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(listingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Listing in the database
        List<Listing> listingList = listingRepository.findAll().collectList().block();
        assertThat(listingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamListing() throws Exception {
        int databaseSizeBeforeUpdate = listingRepository.findAll().collectList().block().size();
        listing.setId(count.incrementAndGet());

        // Create the Listing
        ListingDTO listingDTO = listingMapper.toDto(listing);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(listingDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Listing in the database
        List<Listing> listingList = listingRepository.findAll().collectList().block();
        assertThat(listingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateListingWithPatch() throws Exception {
        // Initialize the database
        listingRepository.save(listing).block();

        int databaseSizeBeforeUpdate = listingRepository.findAll().collectList().block().size();

        // Update the listing using partial update
        Listing partialUpdatedListing = new Listing();
        partialUpdatedListing.setId(listing.getId());

        partialUpdatedListing.description(UPDATED_DESCRIPTION).price(UPDATED_PRICE).category(UPDATED_CATEGORY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedListing.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedListing))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Listing in the database
        List<Listing> listingList = listingRepository.findAll().collectList().block();
        assertThat(listingList).hasSize(databaseSizeBeforeUpdate);
        Listing testListing = listingList.get(listingList.size() - 1);
        assertThat(testListing.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testListing.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testListing.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testListing.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testListing.getCategory()).isEqualTo(UPDATED_CATEGORY);
    }

    @Test
    void fullUpdateListingWithPatch() throws Exception {
        // Initialize the database
        listingRepository.save(listing).block();

        int databaseSizeBeforeUpdate = listingRepository.findAll().collectList().block().size();

        // Update the listing using partial update
        Listing partialUpdatedListing = new Listing();
        partialUpdatedListing.setId(listing.getId());

        partialUpdatedListing
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .address(UPDATED_ADDRESS)
            .category(UPDATED_CATEGORY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedListing.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedListing))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Listing in the database
        List<Listing> listingList = listingRepository.findAll().collectList().block();
        assertThat(listingList).hasSize(databaseSizeBeforeUpdate);
        Listing testListing = listingList.get(listingList.size() - 1);
        assertThat(testListing.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testListing.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testListing.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testListing.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testListing.getCategory()).isEqualTo(UPDATED_CATEGORY);
    }

    @Test
    void patchNonExistingListing() throws Exception {
        int databaseSizeBeforeUpdate = listingRepository.findAll().collectList().block().size();
        listing.setId(count.incrementAndGet());

        // Create the Listing
        ListingDTO listingDTO = listingMapper.toDto(listing);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, listingDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(listingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Listing in the database
        List<Listing> listingList = listingRepository.findAll().collectList().block();
        assertThat(listingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchListing() throws Exception {
        int databaseSizeBeforeUpdate = listingRepository.findAll().collectList().block().size();
        listing.setId(count.incrementAndGet());

        // Create the Listing
        ListingDTO listingDTO = listingMapper.toDto(listing);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(listingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Listing in the database
        List<Listing> listingList = listingRepository.findAll().collectList().block();
        assertThat(listingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamListing() throws Exception {
        int databaseSizeBeforeUpdate = listingRepository.findAll().collectList().block().size();
        listing.setId(count.incrementAndGet());

        // Create the Listing
        ListingDTO listingDTO = listingMapper.toDto(listing);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(listingDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Listing in the database
        List<Listing> listingList = listingRepository.findAll().collectList().block();
        assertThat(listingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteListing() {
        // Initialize the database
        listingRepository.save(listing).block();

        int databaseSizeBeforeDelete = listingRepository.findAll().collectList().block().size();

        // Delete the listing
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, listing.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Listing> listingList = listingRepository.findAll().collectList().block();
        assertThat(listingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
