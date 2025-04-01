package mobile.mapping;

import mobile.model.Entity.Chapter;
import mobile.model.payload.response.ChapterNewUpdateResponse;
import mobile.model.payload.response.ChapterResponse;

import java.util.ArrayList;
import java.util.List;

public class ChapterMapping {

    public static ChapterResponse toChapterResponse(Chapter chapter, int totalChapters) {
        ChapterResponse response = new ChapterResponse();
        response.setChapterNumber(chapter.getChapterNumber());
        response.setComic(chapter.getComic()); // Simulate DBRef
        response.setName(chapter.getName());
        response.setImages(chapter.getImages());
        response.setCreateAt(chapter.getCreateAt());
        response.setUpdateAt(chapter.getUpdateAt());
        response.setTotalChapters(totalChapters);
        return response;
    }

    public static ChapterNewUpdateResponse getChapterNewUpdateResponse(Chapter chapter){
        ChapterNewUpdateResponse c =new ChapterNewUpdateResponse();
        c.setTheloai(chapter.getComic().getGenre());
        c.setTentruyen(chapter.getComic().getName());
        c.setTacgia(chapter.getComic().getArtist());
        c.setUpdateAt(chapter.getUpdateAt());
        c.setChapnumber(chapter.getChapterNumber());
        c.setNguoidangtruyen(chapter.getComic().getUploader().getFullName());
        c.setTenchap(chapter.getName());
        c.setUrl(chapter.getComic().getUrl());
        return c;
    }
    public static List<ChapterNewUpdateResponse> getListChapterNewUpdateResponse(List<Chapter> chapterList){
        List<ChapterNewUpdateResponse> list = new ArrayList<>();
        for (Chapter chapter:chapterList ) {
            list.add(getChapterNewUpdateResponse(chapter));
        }
        return  list;
    }


}
