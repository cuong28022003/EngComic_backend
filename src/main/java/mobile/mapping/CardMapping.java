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
        if (card.getId() != null) {
            response.setId(card.getId().toHexString());
        }
        if (card.getUserId() != null) {
            response.setUserId(card.getUserId().toHexString());
        }
        if (card.getDeckId() != null) {
            response.setDeckId(card.getDeckId().toHexString());
        }
        response.setFront(card.getFront());
        response.setBack(card.getBack());
        response.setIpa(card.getIpa());
        response.setImage(card.getImage());
        response.setAudio(card.getAudio());
        response.setTags(card.getTags());
        response.setPartOfSpeech(card.getPartOfSpeech());
        response.setLevel(card.getLevel());
        response.setExamples(card.getExamples());
        response.setCollocations(card.getCollocations());
        response.setSynonyms(card.getSynonyms());
        response.setAntonyms(card.getAntonyms());
        response.setWordFamily(card.getWordFamily());
        response.setCommonMistakes(card.getCommonMistakes());
        response.setPersonalNote(card.getPersonalNote());
        response.setMyExample(card.getMyExample());
        response.setFavorite(card.isFavorite());
        response.setMasteryStatus(card.getMasteryStatus() != null ? card.getMasteryStatus() : "NEW");
        if (card.getCreateAt() != null) {
            response.setCreateAt(card.getCreateAt().toString());
        }
        if (card.getUpdateAt() != null) {
            response.setUpdateAt(card.getUpdateAt().toString());
        }
        return response;
    }

    public static Card createRequestToEntity(CreateCardRequest request) {
        Card card = new Card();
        if (request.getUserId() != null && ObjectId.isValid(request.getUserId())) {
            card.setUserId(new ObjectId(request.getUserId()));
        }
        if (request.getDeckId() != null && ObjectId.isValid(request.getDeckId())) {
            card.setDeckId(new ObjectId(request.getDeckId()));
        }
        card.setFront(request.getFront());
        card.setBack(request.getBack());
        card.setIpa(request.getIPA());
        card.setImage(request.getImage());
        card.setAudio(request.getAudio());
        card.setTags(request.getTags());
        card.setPartOfSpeech(request.getPartOfSpeech());
        card.setLevel(request.getLevel());
        card.setExamples(request.getExamples());
        card.setCollocations(request.getCollocations());
        card.setSynonyms(request.getSynonyms());
        card.setAntonyms(request.getAntonyms());
        card.setWordFamily(request.getWordFamily());
        card.setCommonMistakes(request.getCommonMistakes());
        card.setPersonalNote(request.getPersonalNote());
        card.setMyExample(request.getMyExample());
        card.setFavorite(request.isFavorite());
        if (request.getMasteryStatus() != null && !request.getMasteryStatus().isEmpty()) {
            card.setMasteryStatus(request.getMasteryStatus());
        }
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
