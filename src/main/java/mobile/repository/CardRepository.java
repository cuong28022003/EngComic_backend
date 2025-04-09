package mobile.repository;

import mobile.model.Entity.Card;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends MongoRepository<Card, ObjectId> {
    Page<Card> findByDeckId(ObjectId deckId, Pageable pageable);
    Optional<Card> findById(ObjectId id);
    void deleteById(ObjectId id);
    Card save(Card card);
}
