package com.listingsearch.api.listing;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ListingItem(
        String id,
        String source,
        String address,
        String city,
        String state,
        String zip,
        BigDecimal price,
        int bedrooms,
        BigDecimal bathrooms,
        int sqft,
        LocalDate listedDate,
        String status,
        double score
) {
    static ListingItem from(ScoredListing s) {
        Listing l = s.listing();
        return new ListingItem(
                l.getListingId(),
                l.getSource(),
                l.getAddress(),
                l.getCity(),
                l.getState(),
                l.getZip(),
                l.getPrice(),
                l.getBedrooms(),
                l.getBathrooms(),
                l.getSqft(),
                l.getListedDate(),
                l.getStatus(),
                Math.round(s.score() * 100.0) / 100.0
        );
    }
}
