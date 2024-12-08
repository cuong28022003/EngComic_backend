package mobile.mapping;

import mobile.model.Entity.Chapter;
import mobile.model.payload.response.ChapterNewUpdateResponse;

import java.util.ArrayList;
import java.util.List;

public class ChapterMapping {

    public static ChapterNewUpdateResponse getChapterNewUpdateResponse(Chapter chapter){
        ChapterNewUpdateResponse c =new ChapterNewUpdateResponse();
        c.setTheloai(chapter.getDautruyenId().getGenre());
        c.setTentruyen(chapter.getDautruyenId().getName());
        c.setTacgia(chapter.getDautruyenId().getArtist());
        c.setUpdateAt(chapter.getUpdateAt());
        c.setChapnumber(chapter.getChapnumber());
        c.setNguoidangtruyen(chapter.getDautruyenId().getUploader().getTenhienthi());
        c.setTenchap(chapter.getTenchap());
        c.setUrl(chapter.getDautruyenId().getUrl());
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
