package mobile.mapping;

import mobile.model.Entity.Card;
import mobile.model.payload.request.card.CreateCardRequest;
import mobile.model.payload.response.CardResponse;
import org.bson.types.ObjectId;

public class CardMapping {
    public static CardResponse entityToResponse(Card card) {
        CardResponse response = new CardResponse();
        response.setId(card.getId().toHexString());
        response.setDeckId(card.getDeckId().toHexString());
        response.setFront(card.getFront());
        response.setBack(card.getBack());
        response.setIPA(card.getIPA());
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
        card.setIPA(request.getIPA());
        card.setImage(request.getImage());
        card.setAudio(request.getAudio());
        card.setTags(request.getTags());
        return card;
    }
}
