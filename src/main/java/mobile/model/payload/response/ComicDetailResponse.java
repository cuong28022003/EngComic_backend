package mobile.model.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ComicDetailResponse {
    protected String id;
    protected double averageRating;
    protected String image;
    protected int views;
    protected String uploader;
    protected String description;
    protected int totalRatings;
    protected String artist;
    protected String name;
    protected String genre;
    protected String status;
    protected String url;
    protected int chapterCount;
}
