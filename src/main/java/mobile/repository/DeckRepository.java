package mobile.repository;

import mobile.model.Entity.Deck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeckRepository extends MongoRepository<Deck, String> {
    Page<Deck> findByUserId(String userId, Pageable pageable);
    Optional<Deck> findById(String id);
    void deleteById(String id);
    void deleteByUserId(String userId);
    Deck save(Deck deck);
}
