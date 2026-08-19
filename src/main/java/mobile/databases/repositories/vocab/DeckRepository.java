package mobile.databases.repositories.vocab;

import mobile.databases.entities.vocab.DeckEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("vocabDeckRepository")
public interface DeckRepository extends MongoRepository<DeckEntity, String> {
    Page<DeckEntity> findByUserId(String userId, Pageable pageable);
    Optional<DeckEntity> findByIdAndUserId(String id, String userId);
    boolean existsByUserIdAndName(String userId, String name);
    long countByUserId(String userId);
}
