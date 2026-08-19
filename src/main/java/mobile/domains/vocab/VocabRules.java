package mobile.domains.vocab;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Pure domain rules for Vocab Domain (Flashcards SM-2, Leech, Capacity, Deck validation).
 */
public class VocabRules {

    public static final double MIN_EASE_FACTOR = 1.3;
    public static final double DEFAULT_EASE_FACTOR = 2.5;
    public static final int LEECH_THRESHOLD = 8;
    public static final int MATURE_INTERVAL_DAYS = 21;

    public record SrsStateInput(
            int repetition,
            double easeFactor,
            int intervalDays
    ) {}

    public record SrsCalculationResult(
            int nextRepetition,
            double nextEaseFactor,
            int nextIntervalDays,
            Date nextReviewAt
    ) {}

    public record CardProgress(
            String id,
            int repetition,
            int interval,
            double easeFactor,
            int wrongCount,
            int reviewCount,
            Date nextReview,
            boolean isFavorite
    ) {}

    public record DeckStatistics(
            int totalCards,
            int totalNew,
            int totalEasy,
            int totalHard,
            int totalDue
    ) {}

    public static SrsCalculationResult calculateSM2(
            SrsStateInput current,
            int quality
    ) {
        return calculateSM2(quality, current, Instant.now());
    }

    public static SrsCalculationResult calculateSM2(
            int quality,
            SrsStateInput current,
            Instant now
    ) {
        if (quality < 0 || quality > 5) {
            throw new IllegalArgumentException("Quality score must be between 0 and 5, received: " + quality);
        }

        double nextEase = current.easeFactor() + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
        if (nextEase < MIN_EASE_FACTOR) {
            nextEase = MIN_EASE_FACTOR;
        }

        int nextRepetition;
        int nextInterval;

        if (quality < 3) {
            nextRepetition = 0;
            nextInterval = 1;
        } else {
            if (current.repetition() == 0) {
                nextInterval = 1;
            } else if (current.repetition() == 1) {
                nextInterval = 6;
            } else {
                nextInterval = (int) Math.round(current.intervalDays() * nextEase);
            }
            nextRepetition = current.repetition() + 1;
        }

        Instant nextReviewInstant = now.plus(nextInterval, ChronoUnit.DAYS);
        Date nextReviewDate = Date.from(nextReviewInstant);

        return new SrsCalculationResult(nextRepetition, nextEase, nextInterval, nextReviewDate);
    }

    public static boolean isLeech(int wrongCount) {
        return wrongCount >= LEECH_THRESHOLD;
    }

    public static String determineStatus(int intervalDays, int wrongCount) {
        if (isLeech(wrongCount)) {
            return "leech";
        }
        if (intervalDays >= MATURE_INTERVAL_DAYS) {
            return "mature";
        }
        if (intervalDays > 0) {
            return "learning";
        }
        return "new";
    }

    public static String determineStatus(int wrongCount, int intervalDays, int repetition) {
        return determineStatus(intervalDays, wrongCount);
    }

    public static int calculateStage(int intervalDays) {
        if (intervalDays <= 0) return 0;
        if (intervalDays <= 3) return 1;
        if (intervalDays <= 7) return 2;
        if (intervalDays <= 14) return 3;
        if (intervalDays < MATURE_INTERVAL_DAYS) return 4;
        return 5;
    }

    public static int calculateNextStage(int currentStage, int quality, int wrongCount) {
        if (quality < 3) return Math.max(0, currentStage - 1);
        return Math.min(5, currentStage + 1);
    }

    public static boolean isValidDeckName(String name) {
        return name != null && !name.trim().isBlank() && name.trim().length() <= 100;
    }

    public static boolean canAddCardToDeck(long currentCardCount, int maxCapacity) {
        return currentCardCount < maxCapacity;
    }

    public static DeckStatistics calculateStatistics(List<CardProgress> cards, Date now) {
        int total = cards.size();
        int newCount = 0;
        int easyCount = 0;
        int hardCount = 0;
        int dueCount = 0;

        for (CardProgress card : cards) {
            if (card.repetition() == 0 && card.reviewCount() == 0) {
                newCount++;
            } else if (card.easeFactor() >= 2.5) {
                easyCount++;
            } else {
                hardCount++;
            }

            if (card.nextReview() != null && !card.nextReview().after(now)) {
                dueCount++;
            }
        }

        return new DeckStatistics(total, newCount, easyCount, hardCount, dueCount);
    }
}
