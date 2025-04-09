package mobile.repository;

import mobile.model.Entity.CardReview;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardReviewRepository extends MongoRepository<CardReview, ObjectId> {
    List<CardReview> findByUserIdAndNextReviewBefore(ObjectId userId, Date today);
    Optional<CardReview> findByUserIdAndCardId(ObjectId userId, ObjectId cardId);
}
