package one.genchev.bonbon.service.mapper;

import one.genchev.bonbon.domain.Listing;
import one.genchev.bonbon.service.dto.ListingDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Listing} and its DTO {@link ListingDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ListingMapper extends EntityMapper<ListingDTO, Listing> {}
