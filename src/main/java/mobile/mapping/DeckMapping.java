package mobile.mapping;

import mobile.model.Entity.Deck;
import mobile.model.payload.request.deck.CreateDeckRequest;

public class DeckMapping {
    public static Deck createRequestToEntity(CreateDeckRequest request) {
        Deck deck = new Deck();
        deck.setName(request.getName());
        deck.setDescription(request.getDescription());
        return deck;
    }
}
