package com.listingsearch.api.listing;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.util.List;

@ConfigurationProperties(prefix = "listings")
public record ListingProperties(List<Resource> dataFiles) {
}
