package mobile.config;

import mobile.model.Entity.CardReview;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class SrsAlgorithm {
    private static final double MIN_EASE_FACTOR = 1.3;
    private static final double DEFAULT_EASE_FACTOR = 2.5;
    private static final int FIRST_INTERVAL = 1;
    private static final int SECOND_INTERVAL = 3;

    public CardReview calculate(CardReview review, boolean isCorrect) {
        if (isCorrect) {
            review.setRepetition(review.getRepetition() + 1);
            review.setEaseFactor(review.getEaseFactor() + 0.1);

            int interval = calculateNextInterval(review);
            review.setInterval(interval);
        } else {
            review.setRepetition(0);
            review.setEaseFactor(Math.max(MIN_EASE_FACTOR, review.getEaseFactor() - 0.2));
            review.setInterval(FIRST_INTERVAL);
            review.setLapses(review.getLapses() + 1);
        }

        review.setLastReviewed(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, review.getInterval());
        review.setNextReview(calendar.getTime());

        return review;
    }

    private int calculateNextInterval(CardReview review) {
        if (review.getRepetition() == 1) {
            return FIRST_INTERVAL;
        }
        if (review.getRepetition() == 2) {
            return SECOND_INTERVAL;
        }
        return (int) Math.ceil(review.getInterval() * review.getEaseFactor());
    }

    public CardReview initNewReview(ObjectId userId, ObjectId cardId) {
        CardReview review = new CardReview();
        review.setUserId(userId);
        review.setCardId(cardId);
        review.setLastReviewed(new Date());
        review.setNextReview(new Date());
        review.setInterval(FIRST_INTERVAL);
        review.setEaseFactor(DEFAULT_EASE_FACTOR);
        review.setRepetition(0);
        review.setLapses(0);
        return review;
    }
}
