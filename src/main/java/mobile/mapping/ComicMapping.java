package mobile.mapping;

import mobile.model.Entity.Comic;
import mobile.model.payload.request.novel.CreateComicRequest;
import mobile.model.payload.request.novel.UpdateComicRequest;
import mobile.model.payload.response.ComicDetailResponse;
import mobile.model.payload.response.ComicResponse;

public class ComicMapping {
    public static Comic CreateRequestToComic(CreateComicRequest createComicRequest){
        Comic newComic = new Comic();
        newComic.setImage(createComicRequest.getImage());
        newComic.setName(createComicRequest.getName());
        newComic.setArtist(createComicRequest.getArtist());
        newComic.setGenre(createComicRequest.getGenre());
        newComic.setUrl(createComicRequest.getUrl());
        newComic.setRating(0);
        newComic.setDescription(createComicRequest.getDescription());
        return newComic;
    }

    public static ComicDetailResponse EntityToComicDetailResponse(Comic comic, int chapterCount, double rating, int ratingCount){
        ComicDetailResponse comicDetailResponse = new ComicDetailResponse();
        comicDetailResponse.setId(comic.getId().toString());
        comicDetailResponse.setImage(comic.getImage());
        comicDetailResponse.setName(comic.getName());
        comicDetailResponse.setArtist(comic.getArtist());
        comicDetailResponse.setUrl(comic.getUrl());
        comicDetailResponse.setRating(rating);
        comicDetailResponse.setDescription(comic.getDescription());
        comicDetailResponse.setUploader(comic.getUploader().getFullName());
        comicDetailResponse.setViews(comic.getViews());
        comicDetailResponse.setRatingCount(ratingCount);
        comicDetailResponse.setChapterCount(chapterCount);
        comicDetailResponse.setGenre(comic.getGenre());
        comicDetailResponse.setStatus(comic.getStatus());
        return comicDetailResponse;
    }

    public static void UpdateRequestToComic(UpdateComicRequest updateComicRequest, Comic oldComic){
        oldComic.setImage(updateComicRequest.getImage());
        oldComic.setName(updateComicRequest.getName());
        oldComic.setArtist(updateComicRequest.getArtist());
        oldComic.setUrl(updateComicRequest.getUrl());
        oldComic.setDescription(updateComicRequest.getDescription());
        oldComic.setGenre(updateComicRequest.getGenre());   
    }

    public static ComicResponse EntityToComicResponse(Comic comic){
        ComicResponse comicResponse = new ComicResponse();
        comicResponse.setImage(comic.getImage());
        comicResponse.setName(comic.getName());
        comicResponse.setArtist(comic.getArtist());
        comicResponse.setUrl(comic.getUrl());
        comicResponse.setRating(comic.getRating());
        comicResponse.setDescription(comic.getDescription());
        comicResponse.setViews(comic.getViews());
        comicResponse.setRatingCount(comic.getRatingCount());
        comicResponse.setGenre(comic.getGenre());
        comicResponse.setStatus(comic.getStatus());
        return comicResponse;
    }
}
