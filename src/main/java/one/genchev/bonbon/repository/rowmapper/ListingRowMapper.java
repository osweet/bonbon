package one.genchev.bonbon.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import one.genchev.bonbon.domain.Listing;
import one.genchev.bonbon.service.ColumnConverter;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Listing}, with proper type conversions.
 */
@Service
public class ListingRowMapper implements BiFunction<Row, String, Listing> {

    private final ColumnConverter converter;

    public ListingRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Listing} stored in the database.
     */
    @Override
    public Listing apply(Row row, String prefix) {
        Listing entity = new Listing();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", Float.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setCategory(converter.fromRow(row, prefix + "_category", String.class));
        return entity;
    }
}
