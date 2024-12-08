package mobile.model.payload.request.rating;

import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RatingRequest {
    private String url;
    private int rating;
}
