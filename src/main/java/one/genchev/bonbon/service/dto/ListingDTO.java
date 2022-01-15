package one.genchev.bonbon.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link one.genchev.bonbon.domain.Listing} entity.
 */
public class ListingDTO implements Serializable {

    private Long id;

    @Size(max = 100)
    private String name;

    @Size(max = 2000)
    private String description;

    private Float price;

    @Size(max = 100)
    private String address;

    private String category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ListingDTO)) {
            return false;
        }

        ListingDTO listingDTO = (ListingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, listingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ListingDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", address='" + getAddress() + "'" +
            ", category='" + getCategory() + "'" +
            "}";
    }
}
