package mobile.domains.card;

import java.util.Calendar;
import java.util.Date;

public class SrsAlgorithmRules {

    public static SrsCalculationResult calculateSM2(int currentRepetition, int currentInterval, double currentEaseFactor, int quality) {
        int nextRepetition;
        int nextInterval;

        if (quality >= 3) {
            if (currentRepetition == 0) {
                nextInterval = 1;
            } else if (currentRepetition == 1) {
                nextInterval = 6;
            } else {
                nextInterval = (int) Math.round(currentInterval * currentEaseFactor);
            }
            nextRepetition = currentRepetition + 1;
        } else {
            nextRepetition = 0;
            nextInterval = 1;
        }

        double nextEaseFactor = currentEaseFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
        nextEaseFactor = Math.max(1.3, nextEaseFactor);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, Math.max(1, nextInterval));
        Date nextReviewDate = cal.getTime();

        return new SrsCalculationResult(nextRepetition, nextInterval, nextEaseFactor, nextReviewDate);
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
        if (wrongCount >= 8) {
            return "leech";
        } else if (interval >= 21) {
            return "mature";
        } else if (repetition > 0) {
            return "learning";
        }
        return "new";
    }

    public record SrsCalculationResult(int repetition, int interval, double easeFactor, Date nextReviewDate) {}
}
