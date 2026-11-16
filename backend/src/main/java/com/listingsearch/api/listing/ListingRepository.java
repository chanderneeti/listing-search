package com.listingsearch.api.listing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long>,
        JpaSpecificationExecutor<Listing> {

    boolean existsBySourceAndListingId(String source, String listingId);
    boolean existsByCityIgnoreCase(String city);
}







