package mobile.Service.Impl;

import mobile.model.Entity.Rating;
import mobile.repository.RatingRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingServiceImpl {
    @Autowired
    private RatingRepository ratingRepository;

    public List<Rating> getRatingsByComic(ObjectId comicId) {
        return ratingRepository.findByComic_Id(comicId);
    }

    public List<Rating> getRatingsByUser(ObjectId userId) {
        return ratingRepository.findByUser_Id(userId);
    }

    public Rating getUserRatingForComic(ObjectId userId, ObjectId comicId) {
        return ratingRepository.findByUser_IdAndComic_Id(userId, comicId);
    }

    public Rating saveRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    public double getAverageRating(ObjectId comicId) {
        List<Rating> ratings = ratingRepository.findByComic_Id(comicId);
        return ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
    }
}
