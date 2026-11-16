package com.listingsearch.api.search;

import com.listingsearch.api.listing.Listing;
import com.listingsearch.api.listing.ListingRepository;
import com.listingsearch.api.listing.ListingSpecifications;
import com.listingsearch.api.scoring.ListingScorer;
import com.listingsearch.api.scoring.ScoredListing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ListingSearchService {
    private final ListingRepository repository;
    private final ListingScorer scorer;

    public ListingSearchService(ListingRepository repository, ListingScorer scorer) {
        this.repository = repository;
        this.scorer = scorer;
    }

    // DB narrows by the cheap filters; the whole-word keyword check, scoring and paging run in Java because LIKE can't do word boundaries
    @Transactional(readOnly = true)
    public SearchResponse search(SearchCriteria criteria) {
        List<Listing> rows = repository.findAll(ListingSpecifications.from(criteria));

        if (criteria.keyword() != null) {
            rows = rows.stream()
                    .filter(l -> KeywordMatcher.containsWholeWord(l.getDescription(), criteria.keyword()))
                    .toList();
        }
        List<ScoredListing> ranked = scorer.rank(rows, criteria.targetBudget());
        PageResult<ScoredListing> page =
                Paginator.paginate(ranked, criteria.page(), criteria.pageSize());
        List<ListingItem> items = page.items().stream()
                .map(ListingItem::from)
                .toList();
        SearchResponse searchResponse = new SearchResponse(items, page.page(), page.pageSize(), page.totalItems(), page.totalPages(), messageFor(criteria, page));
        log.info("Search {} -> {} results (page {} of {})",
                criteria, page.totalItems(), page.page(), page.totalPages());
        return searchResponse;
    }

    private String messageFor(SearchCriteria criteria, PageResult<?> page) {
        if (page.totalItems() == 0) {
            if (criteria.city() != null && !repository.existsByCityIgnoreCase(criteria.city())) {
                return "No listings in " + criteria.city();
            }
            return "No listings match your search";
        }
        if (page.items().isEmpty()) {
            return "Page " + page.page() + " is past the last page (" + page.totalPages() + ")";
        }
        return null;
    }
}
