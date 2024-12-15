package mobile.Service;

import mobile.model.Entity.Comic;
import mobile.model.Entity.Rating;
import mobile.model.Entity.User;

public interface RatingService {
    Rating rateComic(Comic comic, User user, int rating);
    double calculateAverageRating(Comic comic);
    int getTotalReviews(Comic comic);
    Rating getRatingByComicAndUser(Comic comic, User user);
    void deleteRating(Comic comic, User user);
}
