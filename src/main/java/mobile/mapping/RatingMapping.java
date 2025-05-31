package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.Service.UserService;
import mobile.model.Entity.Rating;
import mobile.model.payload.request.rating.RatingRequest;
import mobile.model.payload.response.RatingResponse;
import mobile.model.payload.response.user.UserResponse;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingMapping {
    private final UserService userService;

    public RatingResponse entityToResponse(Rating rating) {
        UserResponse user = userService.findById(rating.getUserId());

        RatingResponse response = new RatingResponse();
        response.setId(rating.getId().toHexString());
        response.setUser(user);
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
