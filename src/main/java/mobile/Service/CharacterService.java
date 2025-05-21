package mobile.Service;

import mobile.model.Entity.Character;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CharacterService {
    List<Character> findAll();
    Character findById(ObjectId id);
    Character create(String name, String description, String rarity, MultipartFile image, ObjectId packId, int bonusXp, int bonusDiamond, Map<String, Integer> skillsUsagePerDay);
    Character update(ObjectId id, String name, String description, String rarity, MultipartFile image, int bonusXp, int bonusDiamond, Map<String, Integer> skillsUsagePerDay);
    void deleteById(ObjectId id);
}
