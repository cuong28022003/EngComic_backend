package mobile.domains.card;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class CardRulesTest {

    @Test
    @DisplayName("Should reset repetitions and set interval to 1 on quality < 3")
    void testAgainRating() {
        CardRules.SrsStateInput input = new CardRules.SrsStateInput(3, 2.5, 10);
        Instant now = Instant.parse("2026-08-18T10:00:00Z");

        CardRules.SrsCalculationResult result = CardRules.calculateSM2(input, 1, now);

        assertEquals(0, result.nextRepetition());
        assertEquals(1, result.nextIntervalDays());
        assertTrue(result.nextEaseFactor() < 2.5);
        assertNotNull(result.nextReviewAt());
    }

    @Test
    @DisplayName("Should increase repetition and interval correctly on quality = 3")
    void testGoodRatingFirstRepetition() {
        CardRules.SrsStateInput input = new CardRules.SrsStateInput(0, 2.5, 0);
        Instant now = Instant.parse("2026-08-18T10:00:00Z");

        CardRules.SrsCalculationResult result = CardRules.calculateSM2(input, 3, now);

        assertEquals(1, result.nextRepetition());
        assertEquals(1, result.nextIntervalDays());
        assertEquals(2.36, result.nextEaseFactor(), 0.001);
    }

    @Test
    @DisplayName("Should flag leech when wrong count reaches 8")
    void testLeechDetection() {
        String status = CardRules.determineStatus(8, 5, 2);
        assertEquals("leech", status);
    }

    @Test
    @DisplayName("Should classify as mature when interval >= 21")
    void testMatureClassification() {
        String status = CardRules.determineStatus(2, 21, 3);
        assertEquals("mature", status);
    }
}
