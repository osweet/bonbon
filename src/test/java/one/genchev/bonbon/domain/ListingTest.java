package one.genchev.bonbon.domain;

import static org.assertj.core.api.Assertions.assertThat;

import one.genchev.bonbon.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ListingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Listing.class);
        Listing listing1 = new Listing();
        listing1.setId(1L);
        Listing listing2 = new Listing();
        listing2.setId(listing1.getId());
        assertThat(listing1).isEqualTo(listing2);
        listing2.setId(2L);
        assertThat(listing1).isNotEqualTo(listing2);
        listing1.setId(null);
        assertThat(listing1).isNotEqualTo(listing2);
    }
}
