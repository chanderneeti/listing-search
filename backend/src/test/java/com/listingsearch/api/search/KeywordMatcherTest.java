package com.listingsearch.api.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeywordMatcherTest {

    @Test
    void matchesAWholeWordIgnoringCase() {
        assertTrue(KeywordMatcher.containsWholeWord("Pets allowed in the unit", "pets"));
    }

    @Test
    void doesNotMatchInsideAnotherWord() {
        assertFalse(KeywordMatcher.containsWholeWord("new carpet throughout", "pet"));
    }

    @Test
    void descriptionNull() {
        assertFalse(KeywordMatcher.containsWholeWord(null, "pet"));
    }

    @Test
    void dotLiteralMatch() {
        assertFalse(KeywordMatcher.containsWholeWord("axb", "a.b"));
        assertTrue(KeywordMatcher.containsWholeWord("see a.b here", "a.b"));
    }

    @Test
    void keywordWithSurroundingSpaces() {
        assertTrue(KeywordMatcher.containsWholeWord("pets allowed", " pets "));
    }

    @Test
    void matchAtStartOrEnd() {
        assertTrue(KeywordMatcher.containsWholeWord("pets allowed here", "pets"));
        assertTrue(KeywordMatcher.containsWholeWord("These are not allowed -pets", "pets"));
    }

    @Test
    void matchesPetsEvenInNoPets() {
        // Purposely not fixing it as current rule stays - has pets keyword, will fix this situation on demand
        assertTrue(KeywordMatcher.containsWholeWord("No pets allowed", "pets"));
    }

}
