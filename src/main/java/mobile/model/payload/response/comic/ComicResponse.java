package mobile.model.payload.response.comic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.model.payload.response.user.UserResponse;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ComicResponse {
    protected Object id;
    protected String name;
    protected String url;
    protected String description;
    protected String genre;
    protected String artist;
    protected String imageUrl;
    protected String backgroundUrl;
    protected UserResponse uploader;
    protected int views;
    protected double rating;
    protected int totalRatings;
    protected int totalChapters;
    protected String status;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
