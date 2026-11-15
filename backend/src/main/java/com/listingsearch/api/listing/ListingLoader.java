package com.listingsearch.api.listing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

@Component
@Slf4j
public class ListingLoader implements ApplicationRunner {

    private final ListingProperties properties;
    private final ListingRepository repository;
    private final ObjectMapper mapper;

    public ListingLoader(ListingProperties properties, ListingRepository repository,
                         ObjectMapper mapper) {
        this.properties = properties;
        this.repository = repository;
        this.mapper = mapper;
    }

    private Listing toEntity(ListingFeedItem i) {
        Listing l = new Listing();
        l.setListingId(i.id());
        l.setSource(i.source());
        l.setAddress(i.address());
        l.setCity(i.city());
        l.setState(i.state());
        l.setZip(i.zip());
        l.setPrice(i.price());
        l.setBedrooms(i.bedrooms());
        l.setBathrooms(i.bathrooms());
        l.setSqft(i.sqft());
        l.setLatitude(i.latitude());
        l.setLongitude(i.longitude());
        l.setListedDate(i.listedDate());
        l.setStatus(i.status());
        l.setDescription(i.description());
        return l;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        for (Resource file : properties.dataFiles()) {
            int added = 0;
            int skipped = 0;
            try (InputStream in = file.getInputStream()) {
                List<ListingFeedItem> items =
                        mapper.readValue(in, new TypeReference<List<ListingFeedItem>>() {
                        });
                for (ListingFeedItem item : items) {
                    if (repository.existsBySourceAndListingId(item.source(), item.id())) {
                        skipped++;
                        continue;
                    }
                    repository.save(toEntity(item));
                    added++;
                }
            }
            log.info("Loaded {}: {} added, {} already present", file.getFilename(), added, skipped);
        }
    }

}
