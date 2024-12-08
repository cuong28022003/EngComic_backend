package mobile.Service;

import mobile.model.Entity.Rating;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RatingService {
    List<Rating> getRatingsByComic(ObjectId comicId);
    List<Rating> getRatingsByUser(ObjectId userId);
    Rating getUserRatingForComic(ObjectId userId, ObjectId comicId);
    double getAverageRating(ObjectId comicId);
}
