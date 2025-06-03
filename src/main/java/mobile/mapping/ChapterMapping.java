package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.model.Entity.Chapter;
import mobile.model.payload.response.chapter.ChapterResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChapterMapping {

    public ChapterResponse toChapterResponse(Chapter chapter) {
        ChapterResponse response = new ChapterResponse();
        response.setId(chapter.getId().toHexString());
        response.setChapterNumber(chapter.getChapterNumber());
        response.setName(chapter.getName());
        response.setImageUrl(chapter.getImageUrl());
        response.setComicId(chapter.getComicId().toHexString());
        response.setPageUrls(chapter.getPageUrls());
        response.setCreatedAt(chapter.getCreatedAt());
        response.setUpdatedAt(chapter.getUpdatedAt());
        return response;
    }

//    public static ChapterNewUpdateResponse getChapterNewUpdateResponse(Chapter chapter){
//        ChapterNewUpdateResponse c =new ChapterNewUpdateResponse();
//        c.setTheloai(chapter.getComic().getGenre());
//        c.setTentruyen(chapter.getComic().getName());
//        c.setTacgia(chapter.getComic().getArtist());
//        c.setUpdateAt(chapter.getUpdateAt());
//        c.setChapnumber(chapter.getChapterNumber());
//        c.setNguoidangtruyen(chapter.getComic().getUploader().getFullName());
//        c.setTenchap(chapter.getName());
//        c.setUrl(chapter.getComic().getUrl());
//        return c;
//    }
//    public static List<ChapterNewUpdateResponse> getListChapterNewUpdateResponse(List<Chapter> chapterList){
//        List<ChapterNewUpdateResponse> list = new ArrayList<>();
//        for (Chapter chapter:chapterList ) {
//            list.add(getChapterNewUpdateResponse(chapter));
//        }
//        return  list;
//    }


}
