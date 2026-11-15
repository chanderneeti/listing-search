package com.listingsearch.api.listing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
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
