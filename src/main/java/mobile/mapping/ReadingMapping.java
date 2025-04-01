package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.Service.ChapterService;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Reading;
import mobile.model.payload.response.ReadingResponse;
@RequiredArgsConstructor
public class ReadingMapping {
    private final ChapterService chapterService;
    public static ReadingResponse EntityToResponese(Reading reading, int sochap){
        ReadingResponse readingResponse= new ReadingResponse();
        readingResponse.setName(reading.getComic().getName());
        readingResponse.setImage(reading.getComic().getImage());
        readingResponse.setChapterNumber(reading.getChapterNumber());
        readingResponse.setUrl(reading.getComic().getUrl());
        readingResponse.setChapterCount(sochap);
        return readingResponse;
    }

    public static ReadingResponse NovelToResponese(Comic comic, int sochap){
        ReadingResponse readingResponse= new ReadingResponse();
        readingResponse.setName(comic.getName());
        readingResponse.setImage(comic.getImage());
        readingResponse.setChapterNumber(1);
        readingResponse.setUrl(comic.getUrl());
        readingResponse.setChapterCount(sochap);
        return readingResponse;
    }
}
