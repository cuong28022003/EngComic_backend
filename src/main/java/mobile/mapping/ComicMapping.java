package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.Service.ChapterService;
import mobile.Service.RatingService;
import mobile.Service.UserService;
import mobile.model.Entity.Comic;
import mobile.model.payload.response.comic.ComicResponse;
import mobile.model.payload.response.user.UserResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComicMapping {

    private final RatingService ratingService;
    private final ChapterService chapterService;
    private final UserService userService;

    public ComicResponse toComicResponse(Comic comic){
        UserResponse uploaderResponse = userService.findById(comic.getUploaderId());
        ComicResponse comicResponse = new ComicResponse();
        comicResponse.setId(comic.getId().toHexString());
        comicResponse.setImageUrl(comic.getImageUrl());
        comicResponse.setBackgroundUrl(comic.getBackgroundUrl());
        comicResponse.setName(comic.getName());
        comicResponse.setArtist(comic.getArtist());
        comicResponse.setUrl(comic.getUrl());
        comicResponse.setRating(ratingService.calculateAverageRating(comic.getId()));
        comicResponse.setDescription(comic.getDescription());
        comicResponse.setViews(comic.getViews());
        comicResponse.setTotalRatings(ratingService.getTotalReviews(comic.getId()));
        comicResponse.setGenre(comic.getGenre());
        comicResponse.setStatus(comic.getStatus());
        comicResponse.setTotalChapters(chapterService.countChaptersByComicId(comic.getId()));
        comicResponse.setUploader(uploaderResponse);
        comicResponse.setCreatedAt(comic.getCreatedAt());
        comicResponse.setEnglishLevel(comic.getEnglishLevel());
        comicResponse.setAgeRating(comic.getAgeRating());
        return comicResponse;
    }
}
