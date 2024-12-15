package mobile.mapping;

import mobile.model.Entity.Comic;
import mobile.model.payload.request.novel.CreateNovelRequest;
import mobile.model.payload.request.novel.UpdateNovelRequest;
import mobile.model.payload.response.NovelDetailResponse;
import mobile.model.payload.response.NovelResponse;

public class NovelMapping {
    public static Comic CreateRequestToNovel(CreateNovelRequest createNovelRequest){
        Comic newComic = new Comic();
        newComic.setImage(createNovelRequest.getImage());
        newComic.setName(createNovelRequest.getName());
        newComic.setArtist(createNovelRequest.getArtist());
        newComic.setGenre(createNovelRequest.getGenre());
        newComic.setUrl(createNovelRequest.getUrl());
        newComic.setRating(0);
        newComic.setDescription(createNovelRequest.getDescription());
        return newComic;
    }

    public static NovelDetailResponse EntityToNovelDetailResponse(Comic comic, int sochap){
        NovelDetailResponse novelDetailResponse = new NovelDetailResponse();
        novelDetailResponse.setId(comic.getId().toString());
        novelDetailResponse.setImage(comic.getImage());
        novelDetailResponse.setName(comic.getName());
        novelDetailResponse.setArtist(comic.getArtist());
        novelDetailResponse.setUrl(comic.getUrl());
        novelDetailResponse.setRating(comic.getRating());
        novelDetailResponse.setDescription(comic.getDescription());
        novelDetailResponse.setUploader(comic.getUploader().getTenhienthi());
        novelDetailResponse.setViews(comic.getViews());
        novelDetailResponse.setRatingCount(comic.getRatingCount());
        novelDetailResponse.setChapterCount(sochap);
        novelDetailResponse.setGenre(comic.getGenre());
        novelDetailResponse.setStatus(comic.getStatus());
        return novelDetailResponse;
    }

    public static void UpdateRequestToNovel(UpdateNovelRequest updateNovelRequest, Comic oldComic){
        oldComic.setImage(updateNovelRequest.getImage());
        oldComic.setName(updateNovelRequest.getName());
        oldComic.setArtist(updateNovelRequest.getArtist());
        oldComic.setUrl(updateNovelRequest.getUrl());
        oldComic.setDescription(updateNovelRequest.getDescription());
    }

    public static NovelResponse EntityToNovelResponse(Comic comic){
        NovelResponse novelResponse = new NovelResponse();
        novelResponse.setImage(comic.getImage());
        novelResponse.setName(comic.getName());
        novelResponse.setArtist(comic.getArtist());
        novelResponse.setUrl(comic.getUrl());
        novelResponse.setRating(comic.getRating());
        novelResponse.setDescription(comic.getDescription());
        novelResponse.setViews(comic.getViews());
        novelResponse.setRatingCount(comic.getRatingCount());
        novelResponse.setGenre(comic.getGenre());
        novelResponse.setStatus(comic.getStatus());
        return novelResponse;
    }
}
