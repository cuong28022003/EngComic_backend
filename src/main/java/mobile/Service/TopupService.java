package mobile.Service;

import mobile.model.Entity.Topup;
import mobile.model.payload.request.topup.TopupRequest;
import org.bson.types.ObjectId;

import java.util.List;

public interface TopupService {
    Topup save(TopupRequest topupRequest);
    Topup confirmTopup(ObjectId topupId);
    List<Topup> findByUserId(ObjectId userId);
    void cancelTopup(ObjectId topupId);
}
