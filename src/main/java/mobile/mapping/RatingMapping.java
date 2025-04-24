package mobile.mapping;

import mobile.model.Entity.Rating;
import mobile.model.payload.request.rating.RatingRequest;
import mobile.model.payload.response.RatingResponse;
import org.bson.types.ObjectId;

public class RatingMapping {
    public static RatingResponse entityToResponse(Rating rating) {
        RatingResponse response = new RatingResponse();
        response.setId(rating.getId().toHexString());
        response.setUserId(rating.getUserId().toHexString());
        response.setComicId(rating.getComicId().toHexString());
        response.setRating(rating.getRating());
        response.setComment(rating.getComment());
        response.setCreatedAt(rating.getCreatedAt().toString());
        response.setUpdatedAt(rating.getUpdatedAt().toString());
        return response;
    }

    public static Rating requestToEntity(RatingRequest request) {
        Rating rating = new Rating();
        rating.setComicId(new ObjectId(request.getComicId()));
        rating.setUserId(new ObjectId(request.getUserId()));
        rating.setRating(request.getRating());
        rating.setComment(request.getComment());
        return rating;
    }
}
