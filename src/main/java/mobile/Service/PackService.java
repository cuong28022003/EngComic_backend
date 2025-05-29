package mobile.Service;

import mobile.model.Entity.Pack;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PackService {
    Pack getPackById(ObjectId id);
    Pack createPack(String name, String description, MultipartFile image);
    List<Pack> getAllPacks();
    Pack updatePack(ObjectId id, String name, String description, MultipartFile image);
    void deletePack(ObjectId id);
}
