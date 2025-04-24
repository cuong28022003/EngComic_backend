package mobile.repository;

import mobile.model.Entity.Deck;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeckRepository extends MongoRepository<Deck, ObjectId> {
    Page<Deck> findByUserId(ObjectId userId, Pageable pageable);
    Optional<Deck> findById(ObjectId id);
    void deleteById(ObjectId id);
    Deck save(Deck deck);
}
