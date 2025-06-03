package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.Service.ChapterService;
import mobile.Service.ComicService;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Reading;
import mobile.model.payload.response.comic.ComicResponse;
import mobile.model.payload.response.reading.ReadingResponse;
import mobile.repository.comic.ComicRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadingMapping {
    private final ChapterService chapterService;
    private final ComicRepository comicRepository;
    private final ComicMapping comicMapping;

    public ReadingResponse toReadingResponse(Reading reading) {
        if (reading == null) {
            return null;
        }
        Comic comic = comicRepository.findById(reading.getComicId())
                .orElseThrow(() -> new RuntimeException("Comic not found with id: " + reading.getComicId()));
        ComicResponse comicResponse = comicMapping.toComicResponse(comic);
        ReadingResponse response = new ReadingResponse();
        response.setId(reading.getId().toHexString());
        response.setUserId(reading.getUserId().toHexString());
        response.setComic(comicResponse);
        response.setChapterNumber(reading.getChapterNumber());
        return response;
    }
}
