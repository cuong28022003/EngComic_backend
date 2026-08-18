package mobile.domains.card;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Pure domain logic: SM-2 algorithm, stage transitions, and status determination for Flashcards.
 */
public class CardRules {

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

    public static SrsCalculationResult calculateSM2(
            SrsStateInput currentState,
            int quality,
            Instant now
    ) {
        int nextRepetition;
        int nextInterval;

        if (quality >= 3) {
            if (currentState.repetition() == 0) {
                nextInterval = 1;
            } else if (currentState.repetition() == 1) {
                nextInterval = 6;
            } else {
                nextInterval = (int) Math.round(currentState.intervalDays() * currentState.easeFactor());
            }
            nextRepetition = currentState.repetition() + 1;
        } else {
            nextRepetition = 0;
            nextInterval = 1;
        }

        // SM-2 Ease Factor calculation
        double factorDelta = 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02);
        double nextEaseFactor = Math.max(MIN_EASE_FACTOR, currentState.easeFactor() + factorDelta);

        Instant nextReviewInstant = now.plus(Math.max(1, nextInterval), ChronoUnit.DAYS);
        Date nextReviewDate = Date.from(nextReviewInstant);

        return new SrsCalculationResult(nextRepetition, nextEaseFactor, nextInterval, nextReviewDate);
    }

    public static SrsCalculationResult calculateSM2(SrsStateInput currentState, int quality) {
        return calculateSM2(currentState, quality, Instant.now());
    }

    public static int calculateNextStage(int currentStage, int currentRepetition, int quality) {
        if (quality >= 3) {
            if (currentStage == 0) {
                return 1;
            } else if (currentStage < 5 && currentRepetition >= 2) {
                return currentStage + 1;
            }
        }
        return currentStage;
    }

    public static String determineStatus(int wrongCount, int interval, int repetition) {
        if (wrongCount >= LEECH_THRESHOLD) {
            return "leech";
        } else if (interval >= MATURE_INTERVAL_DAYS) {
            return "mature";
        } else if (repetition > 0) {
            return "learning";
        }
        return "new";
    }
}
