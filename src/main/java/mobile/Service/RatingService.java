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
    double calculateAverageRating(Comic comic);
    int getTotalReviews(Comic comic);

    Page<RatingResponse> getRatingsForComic(ObjectId comicId, Pageable pageable);
    RatingResponse createOrUpdateRating(Rating rating);
    List<Rating> getRatingsByComicId(ObjectId comicId);

}
