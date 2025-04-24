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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
@Transactional
@Slf4j
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingRepository ratingRepository;

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
    public double calculateAverageRating(Comic comic) {
//        List<Rating> ratings = ratingRepository.findByComic(comic);
//        return ratings.stream().mapToInt(Rating::getRating).average().orElse(0);
        return 0;
    }

    public int getTotalReviews(Comic comic) {
//        return ratingRepository.findByComic(comic).size();
        return 0;
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

    public Page<Rating> getRatingsForComic(ObjectId comicId, Pageable pageable) {
        return ratingRepository.findByComicIdOrderByCreatedAtDesc(comicId, pageable);
    }

    public Rating createOrUpdateRating(Rating rating) {
        Optional<Rating> existing = ratingRepository.findByComicIdAndUserId(rating.getComicId(), rating.getUserId());
        existing.ifPresent(r -> rating.setId(r.getId())); // update nếu đã tồn tại
        return ratingRepository.save(rating);
    }

    @Override
    public List<Rating> getRatingsByComicId(ObjectId comicId) {
        return ratingRepository.findByComicId(comicId);
    }
}
