package mobile.model.payload.request.rating;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingRequest {
    private String userId;
    private String comicId;
    private int rating;
    private String comment;
}
