package com.listingsearch.api.load;

import com.listingsearch.api.listing.ListingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "listings.data-files=classpath:data/sample_listings.json",
        "spring.datasource.url=jdbc:h2:mem:loadertest;MODE=Oracle;DB_CLOSE_DELAY=-1"})
public class ListingLoaderTest {

    @Autowired
    private ListingRepository repository;

    @Autowired
    private ListingLoader loader;

    @Test
    void loadsTheSampleFile() {
        assertEquals(12, repository.count());
    }

    @Test
    void loadingTwiceDoesNotDuplicate() throws Exception {
        loader.run(null);
        assertEquals(12, repository.count());
    }
}
