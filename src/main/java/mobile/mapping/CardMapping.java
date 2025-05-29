package mobile.mapping;

import mobile.model.Entity.Card;
import mobile.model.payload.request.card.CreateCardRequest;
import mobile.model.payload.response.card.CardResponse;
import mobile.model.payload.response.card.CardReviewResponse;
import org.bson.types.ObjectId;

import java.util.Date;

import static mobile.constant.SrsConstant.*;

public class CardMapping {

    public static CardResponse entityToResponse(Card card) {
        CardResponse response = new CardResponse();
        response.setId(card.getId().toHexString());
        response.setDeckId(card.getDeckId().toHexString());
        response.setFront(card.getFront());
        response.setBack(card.getBack());
        response.setIPA(card.getIpa());
        response.setImage(card.getImage());
        response.setAudio(card.getAudio());
        response.setTags(card.getTags());
        response.setCreateAt(card.getCreateAt().toString());
        response.setUpdateAt(card.getUpdateAt().toString());
        return response;
    }

    public static Card createRequestToEntity(CreateCardRequest request) {
        Card card = new Card();
        card.setDeckId(new ObjectId(request.getDeckId()));
        card.setFront(request.getFront());
        card.setBack(request.getBack());
        card.setIpa(request.getIPA());
        card.setImage(request.getImage());
        card.setAudio(request.getAudio());
        card.setTags(request.getTags());
        card.setLastReviewed(new Date());
        card.setNextReview(new Date());
        card.setInterval(FIRST_INTERVAL);
        card.setEaseFactor(DEFAULT_EASE_FACTOR);
        card.setRepetition(0);
        card.setLapses(0);
        return card;
    }

    public static CardReviewResponse entityToCardReviewResponse(Card cardReview) {
        CardReviewResponse response = new CardReviewResponse();
        response.setCardId(cardReview.getId().toHexString());
        response.setLastReviewed(cardReview.getLastReviewed());
        response.setNextReview(cardReview.getNextReview());
        response.setInterval(cardReview.getInterval());
        response.setEaseFactor(cardReview.getEaseFactor());
        response.setRepetition(cardReview.getRepetition());
        response.setLapses(cardReview.getLapses());
        return response;
    }
}
