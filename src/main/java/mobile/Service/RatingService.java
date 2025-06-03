package mobile.Service;

import mobile.model.Entity.Comic;
import mobile.model.Entity.Rating;
import mobile.model.Entity.User;
import mobile.model.payload.response.RatingResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RatingService {
    double calculateAverageRating(ObjectId comicId);
    int getTotalReviews(ObjectId comicId);

    Page<RatingResponse> getRatingsForComic(ObjectId comicId, Pageable pageable);
    RatingResponse createOrUpdateRating(ObjectId userId, ObjectId comicId, String comment, int rating);
    List<Rating> getRatingsByComicId(ObjectId comicId);

    void deleteAllRatingsByComicId(ObjectId comicId);

}
