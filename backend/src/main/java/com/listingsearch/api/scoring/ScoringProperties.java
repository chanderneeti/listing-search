package com.listingsearch.api.scoring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "search.scoring")
public record ScoringProperties(
        double budgetWeight,
        double recencyWeight,
        double halfLifeDays
) {
}
