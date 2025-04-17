package mobile.Service;

import mobile.model.Entity.Card;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {
     Card findById(ObjectId id);
     Page<Card> findByDeckId(ObjectId deckId, Pageable pageable);
     Card save(Card card);
     void deleteById(ObjectId id);
     Card review(ObjectId cardId, boolean isCorrect, String reviewState);
     List<Card> findAllByDeckId(ObjectId deckId);

     void deleteAllByDeckId(ObjectId deckId);
}
