package mobile.Service;

import mobile.model.payload.response.pack.GachaPackResult;
import org.bson.types.ObjectId;

import java.util.List;

public interface GachaService {
    List<GachaPackResult> roll(ObjectId userId, int count);
}
