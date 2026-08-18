package mobile.domains.deck;

import java.util.Date;
import java.util.List;

/**
 * Pure domain logic: Deck statistics, capacity limits, and mastery evaluation.
 */
public class DeckRules {

    public static final int MAX_CARDS_PER_DECK = 1000;
    public static final int MAX_NEW_CARDS_PER_DAY = 50;

    public record CardProgress(
            String cardId,
            int repetition,
            int interval,
            double easeFactor,
            int wrongCount,
            int reviewCount,
            Date nextReview,
            boolean isFavorite
    ) {}

    public record DeckStatistics(
            long totalCards,
            long totalNew,
            long totalEasy,
            long totalHard,
            long totalDue,
            long matureCount,
            long learningCount,
            long leechCount,
            double masteryPercentage
    ) {}

    public static DeckStatistics calculateStatistics(List<CardProgress> cards, Date now) {
        if (cards == null || cards.isEmpty()) {
            return new DeckStatistics(0, 0, 0, 0, 0, 0, 0, 0, 0.0);
        }

        long totalCards = cards.size();
        long totalNew = 0;
        long totalEasy = 0;
        long totalHard = 0;
        long totalDue = 0;
        long matureCount = 0;
        long learningCount = 0;
        long leechCount = 0;

        for (CardProgress card : cards) {
            if (card.reviewCount() == 0) {
                totalNew++;
            }
            if (card.easeFactor() > 2.5) {
                totalEasy++;
            }
            if (card.easeFactor() <= 2.0) {
                totalHard++;
            }
            if (card.nextReview() != null && card.nextReview().before(now)) {
                totalDue++;
            }

            if (card.wrongCount() >= 8) {
                leechCount++;
            } else if (card.interval() >= 21 || card.repetition() >= 5) {
                matureCount++;
            } else if (card.repetition() > 0) {
                learningCount++;
            }
        }

        double percentage = totalCards > 0 ? (matureCount * 100.0) / totalCards : 0.0;
        double roundedPercentage = Math.round(percentage * 100.0) / 100.0;

        return new DeckStatistics(
                totalCards,
                totalNew,
                totalEasy,
                totalHard,
                totalDue,
                matureCount,
                learningCount,
                leechCount,
                roundedPercentage
        );
    }
}
