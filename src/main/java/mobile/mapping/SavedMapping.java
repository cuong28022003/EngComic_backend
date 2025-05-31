package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.Service.ComicService;
import mobile.model.Entity.Saved;
import mobile.model.payload.response.ComicResponse;
import mobile.model.payload.response.SavedResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SavedMapping {
    private final ComicService comicService;

    public SavedResponse toSavedResponse(Saved saved){
        ComicResponse comic = comicService.findById(saved.getComicId());
        SavedResponse savedResponse = new SavedResponse();
        savedResponse.setId(saved.getId().toHexString());
        savedResponse.setUserId(saved.getUserId().toHexString());
        savedResponse.setComic(comic);
        savedResponse.setCreatedAt(saved.getCreatedAt());
        return savedResponse;
    }

}
