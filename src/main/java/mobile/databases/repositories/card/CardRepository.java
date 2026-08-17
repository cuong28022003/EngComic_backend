package mobile.databases.repositories.card;

import mobile.databases.entities.card.CardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends MongoRepository<CardEntity, String> {
    Page<CardEntity> findByDeckId(String deckId, Pageable pageable);
    Page<CardEntity> findByDeckIdAndNextReviewLessThanEqual(String deckId, Date date, Pageable pageable);
    Optional<CardEntity> findById(String id);
    Optional<CardEntity> findByDeckIdAndBackIgnoreCase(String deckId, String back);
    void deleteById(String id);
    List<CardEntity> findByDeckId(String deckId);
    Page<CardEntity> findByDeckIdIn(List<String> deckIds, Pageable pageable);
    Page<CardEntity> findByDeckIdInAndBackContainingIgnoreCase(List<String> deckIds, String back, Pageable pageable);

    Page<CardEntity> findByUserId(String userId, Pageable pageable);
    List<CardEntity> findByUserId(String userId);
    Page<CardEntity> findByUserIdAndBackContainingIgnoreCaseOrFrontContainingIgnoreCase(String userId, String back, String front, Pageable pageable);

    Optional<CardEntity> findByUserIdAndFrontIgnoreCase(String userId, String front);
    Page<CardEntity> findByUserIdAndStatus(String userId, String status, Pageable pageable);
    Page<CardEntity> findByUserIdAndTopic(String userId, String topic, Pageable pageable);

    List<CardEntity> findByUserIdAndNextReviewLessThanEqual(String userId, Date date);
    long countByUserIdAndStatus(String userId, String status);
    long countByUserIdAndNextReviewLessThanEqual(String userId, Date date);
    long countByUserId(String userId);

    @Query("{ '$and': [ { 'userId': ?0 }, { '$or': [ { 'nextReview': { '$lte': ?1 } }, { 'status': 'new' } ] } ] }")
    List<CardEntity> findDueCardsByUserId(String userId, Date now);

    @Query("{ '$and': [ { 'userId': ?0 }, { 'relations.relatedCardId': ?1 } ] }")
    List<CardEntity> findReverseRelations(String userId, String cardId);

    @Query("{ '$or': [ { 'deckId': { '$in': ?0 } }, { 'deckId': null }, { 'deckId': { '$exists': false } } ] }")
    Page<CardEntity> findByDeckIdInOrDeckIdNull(List<String> deckIds, Pageable pageable);

    @Query("{ '$and': [ { '$or': [ { 'deckId': { '$in': ?0 } }, { 'deckId': null }, { 'deckId': { '$exists': false } } ] }, { '$or': [ { 'back': { '$regex': ?1, '$options': 'i' } }, { 'front': { '$regex': ?1, '$options': 'i' } } ] } ] }")
    Page<CardEntity> findByDeckIdInOrDeckIdNullAndSearch(List<String> deckIds, String search, Pageable pageable);

    Page<CardEntity> findByBackContainingIgnoreCaseOrFrontContainingIgnoreCase(String back, String front, Pageable pageable);

    void deleteAllByDeckId(String deckId);
}
