package com.listingsearch.api.scoring;

import com.listingsearch.api.listing.Listing;

public record ScoredListing(Listing listing, double score) {
}
