package com.listingsearch.api.search;

import java.util.List;

public record SearchResponse(
        List<ListingItem> items,
        int page,
        int pageSize,
        int totalItems,
        int totalPages,
        String message) {
}
