package com.listingsearch.api.listing;

import java.util.List;

public class Paginator {
    private Paginator() {
    }

    public static <T> PageResult<T> paginate(List<T> all, int page, int pageSize) {
        if (page < 1 || pageSize < 1) {
            throw new IllegalArgumentException("page and pageSize must be at least 1");
        }

        int total = all.size();
        int totalPages = (total + pageSize - 1) / pageSize;
        long from = (long) (page - 1) * pageSize;
        List<T> items = from >= total
                ? List.of()
                : List.copyOf(all.subList((int) from, (int) Math.min(total, from + pageSize)));
        return new PageResult<>(items, page, pageSize, total, totalPages);
    }
}
