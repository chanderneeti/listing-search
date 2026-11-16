package com.listingsearch.api.load;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ListingFeedItem(String id, String source, String address, String city,
                              String state, String zip, BigDecimal price, int bedrooms,
                              double bathrooms, int sqft, Double latitude, Double longitude,
                              LocalDate listedDate, String status, String description) {
}
