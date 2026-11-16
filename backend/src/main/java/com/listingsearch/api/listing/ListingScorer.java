package com.listingsearch.api.listing;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Component
public class ListingScorer {

    private static final Comparator<ScoredListing> ORDER = Comparator
            .comparingDouble(ScoredListing::score).reversed()
            .thenComparing(s -> s.listing().getListedDate(), Comparator.<LocalDate>reverseOrder())
            .thenComparing(s -> s.listing().getPrice())
            .thenComparing(s -> s.listing().getSource())
            .thenComparing(s -> s.listing().getListingId());

    private final ScoringProperties properties;
    private final Clock clock;

    public ListingScorer(ScoringProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public double score(Listing listing, BigDecimal targetBudget) {
        double recency = recency(listing.getListedDate());

        if (targetBudget == null) {
            return 100 * recency;
        }

        return 100 * (properties.budgetWeight() * budgetFit(listing.getPrice(), targetBudget)
                + properties.recencyWeight() * recency);
    }

    public List<ScoredListing> rank(List<Listing> listings, BigDecimal targetBudget) {
        return listings.stream()
                .map(l -> new ScoredListing(l, score(l, targetBudget)))
                .sorted(ORDER)
                .toList();
    }

    private double budgetFit(BigDecimal price, BigDecimal target) {
        double t = target.doubleValue();
        return Math.max(0, 1 - Math.abs(price.doubleValue() - t) / t);
    }

    private double recency(LocalDate listedDate) {
        long ageInDays = Math.max(0, ChronoUnit.DAYS.between(listedDate, LocalDate.now(clock)));
        return Math.pow(0.5, ageInDays / properties.halfLifeDays());
    }
}