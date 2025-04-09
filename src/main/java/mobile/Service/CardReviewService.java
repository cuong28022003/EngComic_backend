package mobile.Service;

import mobile.model.Entity.CardReview;
import org.bson.types.ObjectId;

import java.util.List;

public interface CardReviewService {
    List<CardReview> getDueCardReviews(ObjectId userId);
    CardReview updateReview(ObjectId userId, ObjectId cardId, boolean isCorrect);
}
