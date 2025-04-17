package mobile.config;

import mobile.model.Entity.Card;
import org.springframework.stereotype.Component;

import java.util.Date;

import static mobile.constant.SrsConstant.*;

@Component
public class SrsAlgorithm {


    public Card updateCardReview(Card cardReview, boolean isCorrect , String reviewState) {
        int interval = cardReview.getInterval();
        double easeFactor = cardReview.getEaseFactor();

        if (isCorrect) {
            cardReview.setRepetition(cardReview.getRepetition() + 1);
            cardReview.setEaseFactor(cardReview.getEaseFactor() + 10);
        } else {
            cardReview.setRepetition(0);
            cardReview.setEaseFactor(Math.max(MIN_EASE_FACTOR, cardReview.getEaseFactor() - 20));
            cardReview.setInterval(FIRST_INTERVAL);
            cardReview.setLapses(cardReview.getLapses() + 1);
        }

        switch (reviewState) {
            case "AGAIN":
                interval = 1;
                easeFactor = Math.max(130, easeFactor - AGAIN_EASE_REDUCE);
                break;
            case "HARD":
                interval = (int) (interval * 1.2);
                easeFactor = Math.max(130, easeFactor - HARD_EASE_REDUCE);
                break;
            case "GOOD":
                interval = (int) (interval * easeFactor);
                break;
            case "EASY":
                interval = (int) (interval * easeFactor * 1.5);
                easeFactor += EASY_EASE_INCREASE;
                break;
        }

        // Đảm bảo hệ số dễ tối thiểu
        easeFactor = Math.max(easeFactor, MIN_EASE_FACTOR);

        cardReview.setInterval(interval);
        cardReview.setEaseFactor(easeFactor);
        cardReview.setNextReview(new Date(System.currentTimeMillis() + (long) cardReview.getInterval() * 24 * 60 * 60 * 1000));
        cardReview.setLastReviewed(new Date());

        return cardReview;
    }
}
