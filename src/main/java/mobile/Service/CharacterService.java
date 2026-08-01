package mobile.Service;

import mobile.model.Entity.Character;
import mobile.model.payload.response.character.CharacterResponse;
import mobile.model.payload.response.character.FullCharacterResponse;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CharacterService {
    List<Character> findAll();
    CharacterResponse findById(String id);
    FullCharacterResponse findFullById(String id);
    Character create(String name, String description, String rarity, MultipartFile image, String packId, int bonusXp, int bonusDiamond, String version, Map<String, Integer> skillsUsagePerDay);
    Character update(String id, String name, String description, String rarity, MultipartFile image, int bonusXp, int bonusDiamond, String version, Map<String, Integer> skillsUsagePerDay);
    void deleteById(String id);

    List<Character> findByVersion(String version);
    List<CharacterResponse> findRandomEnemies(int count);
}
