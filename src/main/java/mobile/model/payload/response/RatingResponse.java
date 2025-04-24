package mobile.model.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
    private String id;
    private String userId;
    private String comicId;
    private int rating;
    private String comment;
    private String createdAt;
    private String updatedAt;
}
