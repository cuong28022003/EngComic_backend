package mobile.Service;

import mobile.model.Entity.Deck;
import mobile.model.payload.response.deck.DeckStatisticsResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

public interface DeckService {
    Page<Deck> findByUserId(ObjectId userId, Pageable pageable);
    Deck findById(ObjectId id);
    Deck save(Deck deck);
    void deleteById(ObjectId id);

    DeckStatisticsResponse getDeckStatistics(ObjectId deckId);
}
