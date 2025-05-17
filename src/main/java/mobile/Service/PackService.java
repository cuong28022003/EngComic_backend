package mobile.Service;

import mobile.model.Entity.Pack;
import org.bson.types.ObjectId;

public interface PackService {
    Pack getPackById(ObjectId id);
}
