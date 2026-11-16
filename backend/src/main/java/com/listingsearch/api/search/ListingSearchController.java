package com.listingsearch.api.search;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/listings")
public class ListingSearchController {

    private final ListingSearchService service;

    public ListingSearchController(ListingSearchService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public SearchResponse search(@Valid SearchCriteria criteria) {
        return service.search(criteria);
    }
}
