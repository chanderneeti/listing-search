package com.listingsearch.api.listing;

import java.util.regex.Pattern;

public class KeywordMatcher {
    private KeywordMatcher() {
    }

    public static boolean containsWholeWord(String text, String keyword) {
        if (text == null || keyword == null || keyword.isBlank()) {
            return false;
        }

        Pattern pattern = Pattern.compile(
                "(?<![\\p{L}\\p{N}])" + Pattern.quote(keyword.trim()) + "(?![\\p{L}\\p{N}])",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );
        return pattern.matcher(text).find();
    }
}
