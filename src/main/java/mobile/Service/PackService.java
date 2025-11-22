package mobile.Service;

import mobile.model.Entity.Pack;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PackService {
    Pack getPackById(String id);
    Pack createPack(String name, String description, MultipartFile image);
    List<Pack> getAllPacks();
    Pack updatePack(String id, String name, String description, MultipartFile image);
    void deletePack(String id);
}
