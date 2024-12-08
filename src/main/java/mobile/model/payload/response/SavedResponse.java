package mobile.model.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
public class SavedResponse {
    protected String name;
    protected String image;
    protected String url;
    protected String artist;
    protected ObjectId id;
}
