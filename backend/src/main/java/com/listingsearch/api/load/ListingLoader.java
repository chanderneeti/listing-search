package com.listingsearch.api.load;

import com.listingsearch.api.listing.Listing;
import com.listingsearch.api.listing.ListingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.math.BigDecimal;
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
        l.setBathrooms(toDecimal(i.bathrooms()));
        l.setSqft(i.sqft());
        l.setLatitude(toDecimal(i.latitude()));
        l.setLongitude(toDecimal(i.longitude()));
        l.setListedDate(i.listedDate());
        l.setStatus(i.status());
        l.setDescription(i.description());
        return l;
    }

    // BigDecimal.valueOf(double) avoids binary noise like 1.5000000000000001
    private BigDecimal toDecimal(Double value) {
        return value == null ? null : BigDecimal.valueOf(value);
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
