package mobile.Service;

import mobile.model.Entity.Character;
import mobile.model.payload.response.card.GachaPackResult;
import org.bson.types.ObjectId;

import java.util.List;

public interface GachaService {
    List<GachaPackResult> roll(int count);
}
