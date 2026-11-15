package com.listingsearch.api.listing;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record SearchCriteria(
        @PositiveOrZero BigDecimal minPrice,
        @PositiveOrZero BigDecimal maxPrice,
        @PositiveOrZero Integer minBedrooms,
        String city,
        String keyword,
        @Positive BigDecimal targetBudget,
        @Pattern(regexp = "active|pending|sold", message = "must be active, pending or sold") String status,
        @Min(1) Integer page,
        @Min(1) @Max(50) Integer pageSize) {

    public SearchCriteria {
        city = clean(city);
        keyword = clean(keyword);
        status = status == null || status.isBlank() ? "active" : status.trim().toLowerCase();
        page = page == null ? 1 : page;
        pageSize = pageSize == null ? 10 : pageSize;
    }

    @AssertTrue(message = "minPrice must not exceed maxPrice")
    public boolean isPriceRangeValid() {
        return minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) <= 0;
    }

    private static String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}