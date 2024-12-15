package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.Service.RatingService;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Rating;
import mobile.model.Entity.User;
import mobile.repository.RatingRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor
@Transactional
@Slf4j
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    public Rating rateComic(Comic comic, User user, int rating) {
        Rating existingRating = ratingRepository.findByComicAndUser(comic, user);
        if (existingRating == null) {
            existingRating = new Rating();
            existingRating.setComic(comic);
            existingRating.setUser(user);
        }
        existingRating.setRating(rating);
        return ratingRepository.save(existingRating);
    }

    public double calculateAverageRating(Comic comic) {
        List<Rating> ratings = ratingRepository.findByComic(comic);
        return ratings.stream().mapToInt(Rating::getRating).average().orElse(0);
    }

    public int getTotalReviews(Comic comic) {
        return ratingRepository.findByComic(comic).size();
    }

    @Override
    public Rating getRatingByComicAndUser(Comic comic, User user) {
        return ratingRepository.findByComicAndUser(comic, user);
    }

    @Override
    public void deleteRating(Comic comic, User user) {
        ratingRepository.deleteByComicAndUser(comic, user);
    }
}
