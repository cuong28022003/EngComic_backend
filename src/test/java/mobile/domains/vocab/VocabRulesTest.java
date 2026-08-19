package mobile.domains.vocab;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class VocabRulesTest {

    @Test
    @DisplayName("Should reset repetitions and set interval to 1 on quality < 3")
    void testAgainRating() {
        VocabRules.SrsStateInput input = new VocabRules.SrsStateInput(3, 2.5, 10);
        Instant now = Instant.parse("2026-08-18T10:00:00Z");

        VocabRules.SrsCalculationResult result = VocabRules.calculateSM2(1, input, now);

        assertEquals(0, result.nextRepetition());
        assertEquals(1, result.nextIntervalDays());
        assertTrue(result.nextEaseFactor() < 2.5);
        assertNotNull(result.nextReviewAt());
    }

    @Test
    @DisplayName("Should increase repetition and interval correctly on quality = 3")
    void testGoodRatingFirstRepetition() {
        VocabRules.SrsStateInput input = new VocabRules.SrsStateInput(0, 2.5, 0);
        Instant now = Instant.parse("2026-08-18T10:00:00Z");

        VocabRules.SrsCalculationResult result = VocabRules.calculateSM2(3, input, now);

        assertEquals(1, result.nextRepetition());
        assertEquals(1, result.nextIntervalDays());
        assertEquals(2.36, result.nextEaseFactor(), 0.001);
    }

    @Test
    @DisplayName("Should flag leech when wrong count reaches 8")
    void testLeechDetection() {
        String status = VocabRules.determineStatus(5, 8);
        assertEquals("leech", status);
    }

    @Test
    @DisplayName("Should classify as mature when interval >= 21")
    void testMatureClassification() {
        String status = VocabRules.determineStatus(21, 2);
        assertEquals("mature", status);
    }

    @Test
    @DisplayName("Should validate deck name correctly")
    void testDeckNameValidation() {
        assertTrue(VocabRules.isValidDeckName("IELTS 7.0"));
        assertFalse(VocabRules.isValidDeckName(""));
        assertFalse(VocabRules.isValidDeckName(null));
    }
}
