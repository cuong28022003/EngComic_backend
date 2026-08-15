package mobile.repository;

import mobile.model.Entity.Card;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends MongoRepository<Card, ObjectId> {
    Page<Card> findByDeckId(ObjectId deckId, Pageable pageable);
    Page<Card> findByDeckIdAndNextReviewLessThanEqual(ObjectId deckId, Date date, Pageable pageable);
    Optional<Card> findById(ObjectId id);
    Optional<Card> findByDeckIdAndBackIgnoreCase(ObjectId deckId, String back);
    void deleteById(ObjectId id);
    Card save(Card card);
    List<Card> findByDeckId(ObjectId deckId);
    Page<Card> findByDeckIdIn(List<ObjectId> deckIds, Pageable pageable);
    Page<Card> findByDeckIdInAndBackContainingIgnoreCase(List<ObjectId> deckIds, String back, Pageable pageable);

    Page<Card> findByUserId(ObjectId userId, Pageable pageable);
    Page<Card> findByUserIdAndBackContainingIgnoreCaseOrFrontContainingIgnoreCase(ObjectId userId, String back, String front, Pageable pageable);

    @Query("{ '$or': [ { 'deckId': { '$in': ?0 } }, { 'deckId': null }, { 'deckId': { '$exists': false } } ] }")
    Page<Card> findByDeckIdInOrDeckIdNull(List<ObjectId> deckIds, Pageable pageable);

    @Query("{ '$and': [ { '$or': [ { 'deckId': { '$in': ?0 } }, { 'deckId': null }, { 'deckId': { '$exists': false } } ] }, { '$or': [ { 'back': { '$regex': ?1, '$options': 'i' } }, { 'front': { '$regex': ?1, '$options': 'i' } } ] } ] }")
    Page<Card> findByDeckIdInOrDeckIdNullAndSearch(List<ObjectId> deckIds, String search, Pageable pageable);

    Page<Card> findByBackContainingIgnoreCaseOrFrontContainingIgnoreCase(String back, String front, Pageable pageable);

    void deleteAllByDeckId(ObjectId deckId);
}
