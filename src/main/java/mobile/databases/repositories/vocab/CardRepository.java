package mobile.databases.repositories.vocab;

import mobile.databases.entities.vocab.CardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository("vocabCardRepository")
public interface CardRepository extends MongoRepository<CardEntity, String> {
    Page<CardEntity> findByUserIdAndDeckId(String userId, String deckId, Pageable pageable);
    Page<CardEntity> findByUserId(String userId, Pageable pageable);
    List<CardEntity> findByUserId(String userId);
    List<CardEntity> findByDeckId(String deckId);
    Page<CardEntity> findByDeckId(String deckId, Pageable pageable);
    Page<CardEntity> findByDeckIdAndNextReviewLessThanEqual(String deckId, Date nextReview, Pageable pageable);
    Optional<CardEntity> findByIdAndUserId(String id, String userId);
    Optional<CardEntity> findByUserIdAndWordIgnoreCase(String userId, String word);

    @Query("{ 'userId': ?0, 'status': { $in: ['learning', 'mature'] }, 'nextReview': { $lte: ?1 } }")
    List<CardEntity> findDueCards(String userId, Date now, Pageable pageable);

    @Query("{ 'userId': ?0, 'status': { $in: ['learning', 'mature'] }, 'nextReview': { $lte: ?1 } }")
    List<CardEntity> findDueCardsByUserId(String userId, Date now);

    @Query("{ 'userId': ?0, 'status': 'new' }")
    List<CardEntity> findNewCards(String userId, Pageable pageable);

    long countByUserId(String userId);
    long countByUserIdAndStatus(String userId, String status);
    long countByUserIdAndDeckId(String userId, String deckId);
    long countByUserIdAndNextReviewLessThanEqual(String userId, Date nextReview);
    void deleteByUserIdAndDeckId(String userId, String deckId);
    void deleteAllByDeckId(String deckId);

    Page<CardEntity> findByUserIdAndMeaningContainingIgnoreCaseOrWordContainingIgnoreCase(String userId, String meaning, String word, Pageable pageable);
    Page<CardEntity> findByUserIdAndStatus(String userId, String status, Pageable pageable);
    List<CardEntity> findByUserIdAndStatus(String userId, String status);
    List<CardEntity> findByUserIdAndDeckId(String userId, String deckId);
    Page<CardEntity> findByUserIdAndTopicContainingIgnoreCase(String userId, String topic, Pageable pageable);

    @Query("{ 'userId': ?0, '$or': [ { 'relations.relatedCardId': ?1 }, { 'relations.text': { $regex: ?1, $options: 'i' } }, { 'relations.word': { $regex: ?1, $options: 'i' } } ] }")
    List<CardEntity> findReverseRelations(String userId, String targetWordOrId);

    @Query("{ 'deckId': { $in: ?0 }, '$or': [ { 'word': { $regex: ?1, $options: 'i' } }, { 'meaning': { $regex: ?1, $options: 'i' } }, { 'front': { $regex: ?1, $options: 'i' } }, { 'back': { $regex: ?1, $options: 'i' } } ] }")
    Page<CardEntity> findByDeckIdInOrDeckIdNullAndSearch(List<String> deckIds, String search, Pageable pageable);

    @Query("{ 'deckId': { $in: ?0 } }")
    Page<CardEntity> findByDeckIdInOrDeckIdNull(List<String> deckIds, Pageable pageable);
}
