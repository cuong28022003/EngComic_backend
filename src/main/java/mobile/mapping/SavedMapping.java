package mobile.mapping;

import mobile.model.Entity.Saved;
import mobile.model.payload.response.SavedResponse;

import java.util.ArrayList;
import java.util.List;

public class SavedMapping {
    public static SavedResponse EntityToResponse(Saved saved){
        SavedResponse savedResponse = new SavedResponse();
        savedResponse.setName(saved.getComic().getName());
        savedResponse.setImage(saved.getComic().getImage());
        savedResponse.setUrl(saved.getComic().getUrl());
        savedResponse.setArtist(saved.getComic().getArtist());
        savedResponse.setId(saved.getId());
        return savedResponse;
    }
    public static List<SavedResponse> ListEntityToResponse(List<Saved> savedList){
        List<SavedResponse> savedResponseList = new ArrayList<>();
        for (Saved saved: savedList ) {
            try{
                savedResponseList.add(EntityToResponse(saved));
            }
            catch (Exception ex){

            }

        }
        return savedResponseList;
    }
}
