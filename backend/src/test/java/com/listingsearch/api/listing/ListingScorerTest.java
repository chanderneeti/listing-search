package com.listingsearch.api.listing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListingScorerTest {
    private final Clock clock =
            Clock.fixed(Instant.parse("2026-11-15T12:00:00Z"), ZoneOffset.UTC);

    private final ListingScorer scorer =
            new ListingScorer(new ScoringProperties(0.6, 0.4, 45), clock);

    private Listing listing(String id, String source, String price, LocalDate listedDate) {
        Listing l = new Listing();
        l.setListingId(id);
        l.setSource(source);
        l.setPrice(new BigDecimal(price));
        l.setListedDate(listedDate);
        return l;
    }

    @Test
    void exactBudgetListedTodayScoredOneHundred() {
        Listing l = listing("1", "A", "400000", LocalDate.of(2026, 11, 15));
        assertEquals(100.0, scorer.score(l, new BigDecimal("400000")), 0.0001);
    }

    @Test
    void withoutBudgetOnlyRecencyCounts() {
        Listing l = listing("1", "A", "400000", LocalDate.of(2026, 10, 1)); // 45 days old
        assertEquals(50.0, scorer.score(l, null), 0.0001);
    }

    @Test
    void postedTodayWithPriceDoubleTheTarget() {
        Listing l = listing("1", "A", "800000", LocalDate.of(2026, 11, 15));
        assertEquals(40.0, scorer.score(l, new BigDecimal("400000")), 0.0001);
    }

    @Test
    void between() {
        //a listing 20% above the target budget and posted today scores 100 × (0.6 × 0.8 + 0.4) = 88.
        Listing l = listing("1", "A", "480000", LocalDate.of(2026, 11, 15));
        assertEquals(88.0, scorer.score(l, new BigDecimal("400000")), 0.0001);
    }

    @Test
    void listingWithFutureDate() {
        //a listed date after today scores the same as today (age 0).
        Listing l = listing("1", "A", "400000", LocalDate.of(2026, 11, 16));
        assertEquals(100.0, scorer.score(l, new BigDecimal("400000")), 0.0001);
    }

    @Test
    void testRankWithHighestFirst() {
        Listing l1 = listing("1", "A", "400000", LocalDate.of(2026, 11, 15));
        Listing l2 = listing("1", "A", "450000", LocalDate.of(2026, 11, 15));
        Listing l3 = listing("1", "A", "500000", LocalDate.of(2026, 11, 15));
        List<Listing> listings = List.of(l1, l2, l3);
        List<ScoredListing> rankedListings = scorer.rank(listings, new BigDecimal("400000"));

        assertEquals(l1.getListingId(), rankedListings.get(0).listing().getListingId());
        assertEquals(l2.getListingId(), rankedListings.get(1).listing().getListingId());
        assertEquals(l3.getListingId(), rankedListings.get(2).listing().getListingId());
    }

    @Test
    void testRankWithSameScoreNewestDateFirst() {
        Listing l1 = listing("1", "A", "400000", LocalDate.of(2026, 11, 15));
        Listing l2 = listing("2", "B", "400000", LocalDate.of(2026, 11, 20));


        List<Listing> listings = List.of(l1, l2);
        List<ScoredListing> rankedListings = scorer.rank(listings, new BigDecimal("400000"));

        assertEquals(l2.getListingId(), rankedListings.get(0).listing().getListingId());
        assertEquals(l1.getListingId(), rankedListings.get(1).listing().getListingId());
    }

    @Test
    void testRankWithSameScoreLowestPriceFirst() {
        Listing l1 = listing("1", "A", "500000", LocalDate.of(2026, 11, 15));
        Listing l2 = listing("2", "B", "450000", LocalDate.of(2026, 11, 15));


        List<Listing> listings = List.of(l1, l2);
        List<ScoredListing> rankedListings = scorer.rank(listings, new BigDecimal("400000"));

        assertEquals(l2.getListingId(), rankedListings.get(0).listing().getListingId());
        assertEquals(l1.getListingId(), rankedListings.get(1).listing().getListingId());
    }

    @Test
    void testRankWithSameDataSourceIdDecides() {
        Listing l1 = listing("1", "A", "400000", LocalDate.of(2026, 11, 15));
        Listing l2 = listing("2", "B", "400000", LocalDate.of(2026, 11, 15));


        List<Listing> listings = List.of(l1, l2);
        List<ScoredListing> rankedListings = scorer.rank(listings, new BigDecimal("400000"));

        assertEquals(l1.getListingId(), rankedListings.get(0).listing().getListingId());
        assertEquals(l2.getListingId(), rankedListings.get(1).listing().getListingId());
    }

    @Test
    void testRankWithSameDataSourceIdDecidesWithSameListingId() {
        Listing l1 = listing("1", "A", "400000", LocalDate.of(2026, 11, 15));
        Listing l2 = listing("1", "B", "400000", LocalDate.of(2026, 11, 15));


        List<Listing> listings = List.of(l1, l2);
        List<ScoredListing> rankedListings = scorer.rank(listings, new BigDecimal("400000"));

        assertEquals(l1.getListingId(), rankedListings.get(0).listing().getListingId());
        assertEquals(l2.getListingId(), rankedListings.get(1).listing().getListingId());
    }

    private List<String> ids(List<ScoredListing> ranked) {
        return ranked.stream().map(s -> s.listing().getListingId()).toList();
    }

    @Test
    void higherScoreComesFirst() {
        LocalDate today = LocalDate.of(2026, 11, 15);
        List<Listing> input = List.of(
                listing("far", "A", "600000", today),
                listing("exact", "A", "400000", today),
                listing("near", "A", "440000", today));

        List<ScoredListing> ranked = scorer.rank(input, new BigDecimal("400000"));

        assertEquals(List.of("exact", "near", "far"), ids(ranked));
    }


}
