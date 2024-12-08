package mobile.model.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
public class NovelDetailResponse {
    protected String id;
    protected int rating;
    protected String image;
    protected int views;
    protected String uploader;
    protected String description;
    protected int reviewCount;
    protected String artist;
    protected String name;
    protected String genre;
    protected String status;
    protected String url;
    protected int chapterCount;
}
