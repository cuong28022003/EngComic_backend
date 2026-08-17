package mobile.databases.repositories.deck;

import mobile.databases.entities.deck.DeckEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeckRepository extends MongoRepository<DeckEntity, String> {
    Page<DeckEntity> findByUserId(String userId, Pageable pageable);
    Optional<DeckEntity> findById(String id);
}
