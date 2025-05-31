package mobile.Service;

import mobile.model.Entity.Character;
import mobile.model.payload.response.character.CharacterResponse;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CharacterService {
    List<Character> findAll();
    CharacterResponse findById(ObjectId id);
    Character create(String name, String description, String rarity, MultipartFile image, ObjectId packId, int bonusXp, int bonusDiamond, String version, Map<String, Integer> skillsUsagePerDay);
    Character update(ObjectId id, String name, String description, String rarity, MultipartFile image, int bonusXp, int bonusDiamond, String version, Map<String, Integer> skillsUsagePerDay);
    void deleteById(ObjectId id);

    List<Character> findByVersion(String version);
}
