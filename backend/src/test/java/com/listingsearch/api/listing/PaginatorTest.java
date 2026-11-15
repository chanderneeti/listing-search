package com.listingsearch.api.listing;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class PaginatorTest {

    private List<Integer> numbers(int count) {
        return IntStream.rangeClosed(1, count).boxed().toList();
    }

    @Test
    void emptyListGivesNoItemsAndZeroList() {
        PageResult<Integer> result = Paginator.paginate(numbers(0), 1, 10);
        assertTrue(result.items().isEmpty());
        assertEquals(0, result.totalPages());
        assertEquals(0, result.totalItems());
    }

    @Test
    void onOverAFulPageNeedsASecondPage() {
        PageResult<Integer> page2 = Paginator.paginate(numbers(11), 2, 10);
        assertEquals(List.of(11), page2.items());
        assertEquals(2, page2.totalPages());
        assertEquals(11, page2.totalItems());
    }

    @Test
    void oneRow() {
        PageResult<Integer> pageResult = Paginator.paginate(numbers(1), 1, 10);
        assertEquals(List.of(1), pageResult.items());
        assertEquals(1, pageResult.totalPages());
        assertEquals(1, pageResult.totalItems());
    }

    @Test
    void exactlyOnePage() {
        PageResult<Integer> pageResult = Paginator.paginate(numbers(10), 1, 10);
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), pageResult.items());
        assertEquals(1, pageResult.totalPages());
        assertEquals(10, pageResult.totalItems());
    }

    @Test
    void exactlyOnePagePage2() {
        PageResult<Integer> pageResult = Paginator.paginate(numbers(1), 2, 10);
        assertEquals(List.of(), pageResult.items());
        assertEquals(1, pageResult.totalPages());
        assertEquals(1, pageResult.totalItems());
    }

    @Test
    void firstPageOfThree() {
        PageResult<Integer> pageResult = Paginator.paginate(numbers(25), 1, 10);
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), pageResult.items());
        assertEquals(3, pageResult.totalPages());
        assertEquals(25, pageResult.totalItems());
    }

    @Test
    void secondPageOfThree() {
        PageResult<Integer> pageResult = Paginator.paginate(numbers(25), 2, 10);
        assertEquals(List.of(11, 12, 13, 14, 15, 16, 17, 18, 19, 20), pageResult.items());
        assertEquals(3, pageResult.totalPages());
        assertEquals(25, pageResult.totalItems());
    }

    @Test
    void thirdPageOfThree() {
        PageResult<Integer> pageResult = Paginator.paginate(numbers(25), 3, 10);
        assertEquals(List.of(21, 22, 23, 24, 25), pageResult.items());
        assertEquals(3, pageResult.totalPages());
        assertEquals(25, pageResult.totalItems());
    }

    @Test
    void pagePastEndPage() {
        PageResult<Integer> pageResult = Paginator.paginate(numbers(25), 99, 10);
        assertEquals(List.of(), pageResult.items());
        assertEquals(3, pageResult.totalPages());
        assertEquals(25, pageResult.totalItems());
    }

    @Test
    void hugePageNumber() {
        PageResult<Integer> pageResult = Paginator.paginate(numbers(25), Integer.MAX_VALUE, 10);
        assertEquals(List.of(), pageResult.items());
        assertEquals(3, pageResult.totalPages());
        assertEquals(25, pageResult.totalItems());
    }

    @Test
    void inValidInput() {
        // Rows any, page 0, page size 10
        IllegalArgumentException ex1 =
                assertThrows(IllegalArgumentException.class, () -> Paginator.paginate(numbers(25), 0, 10));
        assertTrue(ex1.getMessage().contains("page and pageSize must be at least 1"));

        // Rows any, page 1, page size 0
        IllegalArgumentException ex2 =
                assertThrows(IllegalArgumentException.class, () -> Paginator.paginate(numbers(25), 1, 0));
        assertTrue(ex2.getMessage().contains("page and pageSize must be at least 1"));


    }
}
