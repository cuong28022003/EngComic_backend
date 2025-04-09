package mobile.mapping;

import mobile.model.Entity.CardReview;
import mobile.model.payload.response.CardReviewResponse;

public class CardReviewMapping {
    public static CardReviewResponse entityToResponse(CardReview cardReview) {
        CardReviewResponse response = new CardReviewResponse();
        response.setCardId(cardReview.getCardId().toHexString());
        response.setLastReviewed(cardReview.getLastReviewed());
        response.setNextReview(cardReview.getNextReview());
        response.setInterval(cardReview.getInterval());
        response.setEaseFactor(cardReview.getEaseFactor());
        response.setRepetition(cardReview.getRepetition());
        response.setLapses(cardReview.getLapses());
        return response;
    }
}
