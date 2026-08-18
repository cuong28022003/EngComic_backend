package mobile.apis.comic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.user.dtos.UserProfileDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComicResponseDto {
    private String id;
    private String name;
    private String url;
    private String description;
    private String genre;
    private String artist;
    private String imageUrl;
    private String backgroundUrl;
    private UserProfileDto uploader;
    private int views;
    private double rating;
    private int totalRatings;
    private int totalChapters;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String englishLevel;
    private String ageRating;
}
