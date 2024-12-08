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
        readingResponse.setTentruyen(reading.getNovel().getName());
        readingResponse.setHinhanh(reading.getNovel().getImage());
        readingResponse.setChapnumber(reading.getChapnumber());
        readingResponse.setUrl(reading.getNovel().getUrl());
        readingResponse.setSochap(sochap);
        return readingResponse;
    }

    public static ReadingResponse NovelToResponese(Comic comic, int sochap){
        ReadingResponse readingResponse= new ReadingResponse();
        readingResponse.setTentruyen(comic.getName());
        readingResponse.setHinhanh(comic.getImage());
        readingResponse.setChapnumber(1);
        readingResponse.setUrl(comic.getUrl());
        readingResponse.setSochap(sochap);
        return readingResponse;
    }
}
