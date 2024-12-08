package mobile.model.payload.response;

import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RatingResponse {
    double averageRating;
    private int reviewCount;
}
