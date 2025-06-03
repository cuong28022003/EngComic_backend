package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.Service.RatingService;
import mobile.mapping.RatingMapping;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Rating;
import mobile.model.Entity.User;
import mobile.model.payload.response.RatingResponse;
import mobile.repository.RatingRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    private final RatingMapping ratingMapping;

//    public Rating rateComic(Comic comic, User user, int rating) {
//        Rating existingRating = ratingRepository.findByComicAndUser(comic, user);
//        if (existingRating == null) {
//            existingRating = new Rating();
//            existingRating.setComic(comic);
//            existingRating.setUser(user);
//        }
//        existingRating.setRating(rating);
//        return ratingRepository.save(existingRating);
//    }
//
    @Override
    public double calculateAverageRating(ObjectId comicId) {
        List<Rating> ratings = ratingRepository.findByComicId(comicId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream().mapToDouble(Rating::getRating).sum();
    }

    @Override
    public int getTotalReviews(ObjectId comicId) {
        return ratingRepository.findByComicId(comicId).size();
    }
//
//    @Override
//    public Rating getRatingByComicAndUser(Comic comic, User user) {
//        return ratingRepository.findByComicAndUser(comic, user);
//    }
//
//    @Override
//    public void deleteRating(Comic comic, User user) {
//        ratingRepository.deleteByComicAndUser(comic, user);
//    }

    public Page<RatingResponse> getRatingsForComic(ObjectId comicId, Pageable pageable) {
        Page<Rating> ratings = ratingRepository.findByComicIdOrderByCreatedAtDesc(comicId, pageable);
        return ratings.map(rating -> {
            RatingResponse response = ratingMapping.entityToResponse(rating);
            return response;
        });
    }

    public RatingResponse createOrUpdateRating(ObjectId userId, ObjectId comicId, String comment, int ratingValue) {
        Optional<Rating> existing = ratingRepository.findByComicIdAndUserId(comicId, userId);
        if (existing.isPresent()) {
            // Update existing rating
            Rating existingRating = existing.get();
            existingRating.setRating(ratingValue);
            existingRating.setComment(comment);
            existingRating.setCreatedAt(existingRating.getCreatedAt());
            existingRating.setUpdatedAt(LocalDateTime.now());
            return ratingMapping.entityToResponse(ratingRepository.save(existingRating));
        } else {
            // Create new rating
            Rating rating = new Rating();
            rating.setUserId(userId);
            rating.setComicId(comicId);
            rating.setRating(ratingValue);
            rating.setComment(comment);
            rating.setCreatedAt(LocalDateTime.now());
            rating.setUpdatedAt(LocalDateTime.now());
        return ratingMapping.entityToResponse(ratingRepository.save(rating));
        }
    }

    @Override
    public List<Rating> getRatingsByComicId(ObjectId comicId) {
        return ratingRepository.findByComicId(comicId);
    }

    @Override
    public void deleteAllRatingsByComicId(ObjectId comicId) {
        if (comicId == null) {
            throw new IllegalArgumentException("Comic ID cannot be null");
        }
        ratingRepository.deleteAllByComicId(comicId);
        log.info("Deleted all ratings for comic with id: {}", comicId);
    }
}
