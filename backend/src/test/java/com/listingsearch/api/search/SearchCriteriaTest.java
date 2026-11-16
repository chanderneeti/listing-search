package com.listingsearch.api.search;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchCriteriaTest {
    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    private SearchCriteria criteria(BigDecimal minPrice, BigDecimal maxPrice, Integer minBedrooms,
                                    BigDecimal targetBudget, String status,
                                    Integer page, Integer pageSize) {
        return new SearchCriteria(minPrice, maxPrice, minBedrooms, null, null,
                targetBudget, status, page, pageSize);
    }

    @Test
    void emptyCriteriaGetsDefaultsAndIsValid() {
        SearchCriteria c = criteria(null, null, null, null, null, null, null);
        assertEquals("active", c.status());
        assertEquals(1, c.page());
        assertEquals(10, c.pageSize());
        assertTrue(validator.validate(c).isEmpty());
    }

    @Test
    void minPriceAboveMaxPriceIsInvalid() {
        SearchCriteria c = criteria(new BigDecimal("500000"), new BigDecimal("300000"),
                null, null, null, null, null);
        assertEquals(1, validator.validate(c).size());
    }

    @Test
    void minBedroomNegativeIsInvalid() {
        SearchCriteria c = criteria(null, null,
                -1, null, null, null, null);
        assertEquals(1, validator.validate(c).size());
    }

    @Test
    void targetBudgetZeroIsInvalid() {
        SearchCriteria c = criteria(null, null,
                null, new BigDecimal(0), null, null, null);
        assertEquals(1, validator.validate(c).size());
    }

    @Test
    void validateStatus() {
        SearchCriteria c1 = criteria(null, null,
                null, null, "active", null, null);
        assertEquals(0, validator.validate(c1).size());

        SearchCriteria c2 = criteria(null, null,
                null, null, "pending", null, null);
        assertEquals(0, validator.validate(c2).size());

        SearchCriteria c3 = criteria(null, null,
                null, null, "sold", null, null);
        assertEquals(0, validator.validate(c3).size());

        SearchCriteria c4 = criteria(null, null,
                null, null, "rented", null, null);
        assertEquals(1, validator.validate(c4).size());

        SearchCriteria c5 = criteria(null, null,
                null, null, "SOLD", null, null);
        assertEquals(0, validator.validate(c5).size());
    }

    @Test
    void cityGetTrimmed() {
        SearchCriteria c1 = new SearchCriteria(null, null, null, " ", null,
                null, null, null, null);
        assertEquals(null, c1.city());

        SearchCriteria c2 = new SearchCriteria(null, null, null, " Newark", null,
                null, null, null, null);
        assertEquals("Newark", c2.city());
    }

    @Test
    void validateCity() {
        SearchCriteria c1 = criteria(null, null,
                null, null, null, null, 0);
        assertEquals(1, validator.validate(c1).size());

        SearchCriteria c2 = criteria(null, null,
                null, null, null, null, 1);
        assertEquals(0, validator.validate(c2).size());

        SearchCriteria c3 = criteria(null, null,
                null, null, null, null, 50);
        assertEquals(0, validator.validate(c3).size());

        SearchCriteria c4 = criteria(null, null,
                null, null, null, null, 51);
        assertEquals(1, validator.validate(c4).size());
    }

}
