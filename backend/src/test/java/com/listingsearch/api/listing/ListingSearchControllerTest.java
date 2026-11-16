package com.listingsearch.api.listing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ListingSearchController.class)
public class ListingSearchControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ListingSearchService service;

    private ListingItem item() {
        return new ListingItem("A1", "MLS_A", "1 Main St", "Reston", "VA", "20190",
                new BigDecimal("450000"), 2, new BigDecimal("2.0"), 1100,
                LocalDate.of(2026, 11, 1), "active", 94.0);
    }

    @Test
    void successReturnsAPageOfItems() throws Exception {
        SearchResponse response = new SearchResponse(List.of(item()), 1, 10, 1, 1, null);
        when(service.search(any(SearchCriteria.class))).thenReturn(response);

        mvc.perform(get("/api/listings/search").param("city", "Reston"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value("A1"))
                .andExpect(jsonPath("$.items[0].score").value(94.0));

    }

    @Test
    void noParametersUsesDefaults() throws Exception {
        when(service.search(any(SearchCriteria.class)))
                .thenReturn(new SearchResponse(List.of(), 1, 10, 0, 0, null));

        mvc.perform(get("/api/listings/search"))
                .andExpect(status().isOk());

        ArgumentCaptor<SearchCriteria> captor = ArgumentCaptor.forClass(SearchCriteria.class);
        verify(service).search(captor.capture());
        SearchCriteria criteria = captor.getValue();

        assertEquals("active", criteria.status());
        assertEquals(1, criteria.page());
        assertEquals(10, criteria.pageSize());
        assertNull(criteria.city());
    }

    @Test
    void queryParametersAreBoundIntoTheCriteria() throws Exception {
        when(service.search(any(SearchCriteria.class)))
                .thenReturn(new SearchResponse(List.of(), 2, 5, 0, 0, null));

        mvc.perform(get("/api/listings/search")
                        .param("minPrice", "300000")
                        .param("maxPrice", "500000")
                        .param("minBedrooms", "2")
                        .param("city", "  Reston ")
                        .param("keyword", "garden")
                        .param("targetBudget", "450000")
                        .param("status", "PENDING")
                        .param("page", "2")
                        .param("pageSize", "5"))
                .andExpect(status().isOk());

        ArgumentCaptor<SearchCriteria> captor = ArgumentCaptor.forClass(SearchCriteria.class);
        verify(service).search(captor.capture());
        SearchCriteria c = captor.getValue();

        assertEquals(new BigDecimal("300000"), c.minPrice());
        assertEquals(new BigDecimal("500000"), c.maxPrice());
        assertEquals(2, c.minBedrooms());
        assertEquals("Reston", c.city());          // trimmed
        assertEquals("garden", c.keyword());
        assertEquals(new BigDecimal("450000"), c.targetBudget());
        assertEquals("pending", c.status());       // lowercased
        assertEquals(2, c.page());
        assertEquals(5, c.pageSize());
    }

    @Test
    void emptyResult() throws Exception {
        SearchResponse response = new SearchResponse(List.of(), 1, 10, 0, 0, "No listings in Atlantis");
        when(service.search(any(SearchCriteria.class))).thenReturn(response);

        mvc.perform(get("/api/listings/search").param("city", "Reston"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.message").value("No listings in Atlantis"));

    }

    @ParameterizedTest
    @ValueSource(strings = {
            "minPrice=-1", "minPrice=500000&maxPrice=100000", "minBedrooms=-1",
            "targetBudget=0", "status=rented", "page=0", "pageSize=0", "pageSize=51",
            "minPrice=abc", "page=abc"})
    void invalidInputReturns400(String query) throws Exception {
        mvc.perform(get("/api/listings/search?" + query))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    void badMinPrice() throws Exception {
        mvc.perform(get("/api/listings/search").param("minPrice", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].field").value("minPrice"))
                .andExpect(jsonPath("$.errors[0].message").value("invalid value 'abc'"))
                .andExpect(jsonPath("$.title").value("Invalid search parameters"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
