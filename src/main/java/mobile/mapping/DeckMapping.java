package mobile.mapping;

import mobile.model.Entity.Deck;
import mobile.model.payload.request.deck.CreateDeckRequest;
import mobile.model.payload.response.deck.DeckResponse;

public class DeckMapping {
    public static Deck createRequestToEntity(CreateDeckRequest request) {
        Deck deck = new Deck();
        deck.setName(request.getName());
        deck.setDescription(request.getDescription());
        return deck;
    }


    public static DeckResponse entityToResponse(Deck deck) {
        DeckResponse response = new DeckResponse();
        response.setId(deck.getId().toHexString());
        response.setName(deck.getName());
        response.setDescription(deck.getDescription());
        response.setCreateAt(deck.getCreateAt());
        response.setUpdateAt(deck.getUpdateAt());
        return response;
    }
}
