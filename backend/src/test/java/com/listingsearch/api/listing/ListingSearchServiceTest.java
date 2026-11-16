package com.listingsearch.api.listing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListingSearchServiceTest {

    @Mock
    ListingRepository repository;

    private final Clock clock =
            Clock.fixed(Instant.parse("2026-11-15T12:00:00Z"), ZoneOffset.UTC);
    private ListingSearchService service;

    @BeforeEach
    void setUp() {
        ListingScorer scorer = new ListingScorer(new ScoringProperties(0.6, 0.4, 45), clock);
        service = new ListingSearchService(repository, scorer);
    }

    private Listing listing(String id, String city, String price, int bedrooms,
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
        return l;
    }

    @Test
    void keywordKeepsWholeWordMatchesAndDropsPartialOnes() {
        Listing l1 = listing("1", "Reston", "300000", 2, "active", "pet allowed");
        Listing l2 = listing("2", "Reston", "400000", 2, "active", "new carpet");
        when(repository.findAll(ArgumentMatchers.<Specification<Listing>>any()))
                .thenReturn(List.of(l1, l2));

        SearchCriteria criteria = new SearchCriteria(
                null, null, null, null, "pet", null, null, null, null);

        SearchResponse response = service.search(criteria);

        assertEquals(1, response.totalItems());
        assertEquals("1", response.items().get(0).id());
        assertNull(response.message());
    }

    @Test
    void testMessageSpecificToCityNoMatchFound() {
        when(repository.findAll(ArgumentMatchers.<Specification<Listing>>any()))
                .thenReturn(List.of());

        when(repository.existsByCityIgnoreCase("Atlantis"))
                .thenReturn(false);

        when(repository.existsByCityIgnoreCase("Reston"))
                .thenReturn(true);

        SearchCriteria criteria1 = new SearchCriteria(
                null, null, null, "Atlantis", "pet", null, null, null, null);

        SearchResponse response1 = service.search(criteria1);

        assertEquals(0, response1.totalItems());
        assertNotNull(response1.message());
        assertEquals("No listings in Atlantis", response1.message());


        SearchCriteria criteria2 = new SearchCriteria(
                null, null, null, "Reston", "pet", null, null, null, null);

        SearchResponse response2 = service.search(criteria2);

        assertEquals(0, response2.totalItems());
        assertNotNull(response2.message());
        assertEquals("No listings match your search", response2.message());
    }

    @Test
    void testMessagePagePastLastPage() {
        Listing l1 = listing("1", "Reston", "300000", 2, "active", "pet allowed");
        Listing l2 = listing("2", "Reston", "400000", 2, "active", "new carpet");
        when(repository.findAll(ArgumentMatchers.<Specification<Listing>>any()))
                .thenReturn(List.of(l1, l2));

        SearchCriteria criteria = new SearchCriteria(
                null, null, null, null, null, null, null, 99, 10);

        SearchResponse response = service.search(criteria);

        assertEquals(2, response.totalItems());
        assertNotNull(response.message());
        assertTrue(response.message().contains("Page 99 is past"));

    }

    @Test
    void testNormalPageNoMessage() {
        Listing l1 = listing("1", "Reston", "300000", 2, "active", "pet allowed");
        Listing l2 = listing("2", "Reston", "400000", 2, "active", "new carpet");
        when(repository.findAll(ArgumentMatchers.<Specification<Listing>>any()))
                .thenReturn(List.of(l1, l2));

        SearchCriteria criteria = new SearchCriteria(
                null, null, null, null, null, null, null, 1, 10);

        SearchResponse response = service.search(criteria);

        assertEquals(2, response.totalItems());
        assertNull(response.message());
        assertEquals("1", response.items().get(0).id());
        assertEquals("2", response.items().get(1).id());

    }

}
