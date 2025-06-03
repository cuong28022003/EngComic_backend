package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.Service.ComicService;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Saved;
import mobile.model.payload.response.comic.ComicResponse;
import mobile.model.payload.response.SavedResponse;
import mobile.repository.comic.ComicRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SavedMapping {
    private final ComicRepository comicRepository;
    private final ComicMapping comicMapping;

    public SavedResponse toSavedResponse(Saved saved){
        Comic comic = comicRepository.findById(saved.getComicId())
                .orElseThrow(() -> new RuntimeException("Comic not found with id: " + saved.getComicId()));
        ComicResponse comicResponse = comicMapping.toComicResponse(comic);
        SavedResponse savedResponse = new SavedResponse();
        savedResponse.setId(saved.getId().toHexString());
        savedResponse.setUserId(saved.getUserId().toHexString());
        savedResponse.setComic(comicResponse);
        savedResponse.setCreatedAt(saved.getCreatedAt());
        return savedResponse;
    }

}
