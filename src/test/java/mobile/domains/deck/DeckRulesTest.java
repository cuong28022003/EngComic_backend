package mobile.domains.deck;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeckRulesTest {

    @Test
    @DisplayName("Should correctly summarize empty deck")
    void testEmptyDeck() {
        DeckRules.DeckStatistics summary = DeckRules.calculateStatistics(List.of(), new Date());
        assertEquals(0, summary.totalCards());
        assertEquals(0.0, summary.masteryPercentage());
    }

    @Test
    @DisplayName("Should calculate mastery percentage and stats correctly")
    void testDeckEvaluation() {
        List<DeckRules.CardProgress> list = List.of(
                new DeckRules.CardProgress("1", 5, 25, 2.6, 0, 5, new Date(), false), // mature, easy
                new DeckRules.CardProgress("2", 2, 6, 2.5, 0, 2, new Date(), false),  // learning
                new DeckRules.CardProgress("3", 0, 0, 2.5, 0, 0, new Date(), false),  // new
                new DeckRules.CardProgress("4", 1, 1, 1.8, 8, 4, new Date(), false)   // leech, hard
        );

        DeckRules.DeckStatistics summary = DeckRules.calculateStatistics(list, new Date());
        assertEquals(4, summary.totalCards());
        assertEquals(1, summary.totalNew());
        assertEquals(1, summary.totalEasy());
        assertEquals(1, summary.totalHard());
        assertEquals(1, summary.matureCount());
        assertEquals(1, summary.learningCount());
        assertEquals(1, summary.leechCount());
        assertEquals(25.0, summary.masteryPercentage(), 0.01);
    }
}
