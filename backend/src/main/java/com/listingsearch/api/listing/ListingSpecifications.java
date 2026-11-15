package com.listingsearch.api.listing;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Locale;

public class ListingSpecifications {
    private ListingSpecifications() {
    }

    public static Specification<Listing> priceAtLeast(BigDecimal min) {
        return ((root, query, cb) -> cb.greaterThanOrEqualTo(root.<BigDecimal>get("price"), min));
    }

    public static Specification<Listing> priceAtMost(BigDecimal max) {
        return ((root, query, cb) -> cb.lessThanOrEqualTo(root.<BigDecimal>get("price"), max));
    }

    public static Specification<Listing> bedroomsAtLeast(Integer min) {
        return ((root, query, cb) -> cb.greaterThanOrEqualTo(root.<Integer>get("bedrooms"), min));
    }

    public static Specification<Listing> inCity(String city) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.<String>get("city")), city.toLowerCase(Locale.ROOT));
    }

    public static Specification<Listing> hasStatus(String status) {
        return (root, query, cb) -> cb.equal(root.<String>get("status"), status);
    }

    public static Specification<Listing> descriptionContains(String keyword) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.<String>get("description")),
                likePattern(keyword),
                '\\' );
    }

    private static String likePattern(String keyword) {
        String escaped = keyword.toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + escaped + "%";
    }

    public static Specification<Listing> from(SearchCriteria c) {
        Specification<Listing> spec = hasStatus(c.status()); // status is never null
        if (c.minPrice() != null) spec = spec.and(priceAtLeast(c.minPrice()));
        if (c.maxPrice() != null) spec = spec.and(priceAtMost(c.maxPrice()));
        if (c.minBedrooms() != null) spec = spec.and(bedroomsAtLeast(c.minBedrooms()));
        if (c.city() != null) spec = spec.and(inCity(c.city()));
        if (c.keyword() != null) spec = spec.and(descriptionContains(c.keyword()));
        return spec;
    }
}
