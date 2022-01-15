package one.genchev.bonbon.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListingMapperTest {

    private ListingMapper listingMapper;

    @BeforeEach
    public void setUp() {
        listingMapper = new ListingMapperImpl();
    }
}
