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

//    public static Comic CreateRequestToComic(CreateComicRequest createComicRequest){
//        Comic newComic = new Comic();
//        newComic.setName(createComicRequest.getName());
//        newComic.setArtist(createComicRequest.getArtist());
//        newComic.setGenre(createComicRequest.getGenre());
//        newComic.setUrl(createComicRequest.getUrl());
//        newComic.setRating(0);
//        newComic.setDescription(createComicRequest.getDescription());
//        newComic.setUploader();
//        return newComic;
//    }

//    public static ComicDetailResponse EntityToComicDetailResponse(Comic comic, int chapterCount, double rating, int ratingCount){
//        ComicDetailResponse comicDetailResponse = new ComicDetailResponse();
//        comicDetailResponse.setId(comic.getId().toString());
//        comicDetailResponse.setImage(comic.getImageUrl());
//        comicDetailResponse.setName(comic.getName());
//        comicDetailResponse.setArtist(comic.getArtist());
//        comicDetailResponse.setUrl(comic.getUrl());
//        comicDetailResponse.setAverageRating(rating);
//        comicDetailResponse.setDescription(comic.getDescription());
//        comicDetailResponse.setUploader(comic.getUploader().getFullName());
//        comicDetailResponse.setViews(comic.getViews());
//        comicDetailResponse.setTotalRatings(ratingCount);
//        comicDetailResponse.setChapterCount(chapterCount);
//        comicDetailResponse.setGenre(comic.getGenre());
//        comicDetailResponse.setStatus(comic.getStatus());
//        return comicDetailResponse;
//    }

//    public static void UpdateRequestToComic(UpdateComicRequest updateComicRequest, Comic oldComic){
//        oldComic.setImageUrl(updateComicRequest.getImage());
//        oldComic.setName(updateComicRequest.getName());
//        oldComic.setArtist(updateComicRequest.getArtist());
//        oldComic.setUrl(updateComicRequest.getUrl());
//        oldComic.setDescription(updateComicRequest.getDescription());
//        oldComic.setGenre(updateComicRequest.getGenre());
//    }

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
        return comicResponse;
    }
}
