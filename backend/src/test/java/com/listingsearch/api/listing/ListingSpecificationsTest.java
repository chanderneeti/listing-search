package com.listingsearch.api.listing;

import com.listingsearch.api.search.SearchCriteria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties =
        "spring.datasource.url=jdbc:h2:mem:spectest;MODE=Oracle;DB_CLOSE_DELAY=-1")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ListingSpecificationsTest {

    @Autowired
    ListingRepository repository;

    private void save(String id, String city, String price, int bedrooms,
                      String status, String description) {
        Listing l = new Listing();
        l.setListingId(id);
        l.setSource("TEST");
        l.setAddress(id + " Main St");
        l.setCity(city);
        l.setState("VA");
        l.setZip("22000");
        l.setPrice(new BigDecimal(price));
        l.setBedrooms(bedrooms);
        l.setBathrooms(new BigDecimal("1.0"));
        l.setSqft(1000);
        l.setListedDate(LocalDate.of(2026, 11, 1));
        l.setStatus(status);
        l.setDescription(description);
        repository.save(l);
    }

    @Test
    void priceAtLeastIncludesTheBoundary() {
        save("1", "Reston", "300000", 2, "active", "nice");
        save("2", "Reston", "400000", 2, "active", "nice");

        List<Listing> result = repository.findAll(
                ListingSpecifications.priceAtLeast(new BigDecimal("400000")));

        assertEquals(1, result.size());
        assertEquals("2", result.get(0).getListingId());
    }

    @Test
    void priceAtMostIncludesTheBoundary() {
        save("1", "Reston", "300000", 2, "active", "nice");
        save("2", "Reston", "400000", 2, "active", "nice");

        List<Listing> result = repository.findAll(
                ListingSpecifications.priceAtMost(new BigDecimal("300000")));

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getListingId());
    }

    @Test
    void beedroomsAtLeast() {
        save("1", "Reston", "300000", 2, "active", "nice");
        save("2", "Reston", "400000", 3, "active", "nice");

        List<Listing> result = repository.findAll(
                ListingSpecifications.bedroomsAtLeast(3));

        assertEquals(1, result.size());
        assertEquals("2", result.get(0).getListingId());
    }

    @Test
    void inCity() {
        save("1", "reston", "300000", 2, "active", "nice");
        save("2", "Teston", "400000", 3, "active", "nice");

        List<Listing> result = repository.findAll(
                ListingSpecifications.inCity("Reston"));

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getListingId());
    }

    @Test
    void hasStatus() {
        save("1", "Reston", "300000", 2, "active", "nice");
        save("2", "Reston", "400000", 3, "sold", "nice");

        List<Listing> result = repository.findAll(
                ListingSpecifications.hasStatus("active"));

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getListingId());
    }

    @Test
    void descriptionContains() {
        save("1", "Reston", "300000", 2, "active", "has big Garden");
        save("2", "Reston", "400000", 3, "sold", "has big Lawn");

        List<Listing> result = repository.findAll(
                ListingSpecifications.descriptionContains("garden"));

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getListingId());
    }

    @Test
    void combinesAllGivenFilters() {
        save("1", "Reston", "350000", 3, "active", "bright and quiet");
        save("2", "Reston", "350000", 1, "active", "bright and quiet");  // too few bedrooms
        save("3", "Reston", "900000", 3, "active", "bright and quiet");  // too expensive
        save("4", "Fairfax", "350000", 3, "active", "bright and quiet");  // wrong city
        save("5", "Reston", "350000", 3, "sold", "bright and quiet");  // wrong status

        SearchCriteria criteria = new SearchCriteria(
                new BigDecimal("300000"), new BigDecimal("400000"), 2,
                "reston", null, null, null, null, null);

        List<Listing> result = repository.findAll(ListingSpecifications.from(criteria));

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getListingId());

    }

    @Test
    void emptyCriteriaReturnsOnlyActiveListings() {
        save("1", "Reston", "350000", 3, "active", "x");
        save("2", "Reston", "350000", 3, "sold", "x");

        SearchCriteria criteria = new SearchCriteria(
                null, null, null, null, null, null, null, null, null);

        List<Listing> result = repository.findAll(ListingSpecifications.from(criteria));
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getListingId());
    }
}
