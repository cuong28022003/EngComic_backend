package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterService;
import mobile.Service.CloudinaryService;
import mobile.Service.PackService;
import mobile.mapping.CharacterMapping;
import mobile.model.Entity.Character;
import mobile.model.Entity.Pack;
import mobile.model.payload.response.character.CharacterResponse;
import mobile.repository.character.CharacterRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharacterServiceImpl implements CharacterService {

    private final CharacterMapping characterMapping;
    private final PackService packService;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public List<Character> findAll() {
        return characterRepository.findAll();
    }

    @Override
    public CharacterResponse findById(String id) {
        Character characterCard = characterRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new RuntimeException("CharacterCard not found"));
        return characterMapping.toCharacterResponse(characterCard);
    }

    @Override
    public Character create(String name, String description, String rarity, MultipartFile image, String packId, int bonusXp, int bonusDiamond, String version, Map<String, Integer> skillsUsagePerDay) {
        Character character = new Character();
        character.setName(name);
        character.setDescription(description);
        character.setRarity(rarity);
        character.setPackId(packId);
        character.setBonusXp(bonusXp);
        character.setBonusDiamond(bonusDiamond);
        character.setVersion(version);
        character.setSkillsUsagePerDay(skillsUsagePerDay);

        Pack pack = packService.getPackById(packId);

        try {
            String folder = String.format("packs/%s", pack.getId());
            String imageUrl = cloudinaryService.uploadFile(image, folder);
            character.setImageUrl(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
        return characterRepository.save(character);
    }

    @Override
    public Character update(ObjectId id, String name, String description, String rarity, MultipartFile image, int bonusXp, int bonusDiamond, String version, Map<String, Integer> skillsUsagePerDay) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CharacterCard not found"));

        character.setName(name);
        character.setDescription(description);
        character.setRarity(rarity);
        character.setBonusXp(bonusXp);
        character.setBonusDiamond(bonusDiamond);
        character.setVersion(version);
        character.setSkillsUsagePerDay(skillsUsagePerDay);

        Pack pack = packService.getPackById(character.getPackId());

        try {
            String folder = String.format("packs/%s", pack.getId());
            String imageUrl = cloudinaryService.uploadFile(image, folder);
            character.setImageUrl(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
        return characterRepository.save(character);
    }

    @Override
    public void deleteById(ObjectId id) {
        if (!characterRepository.existsById(id)) {
            throw new RuntimeException("CharacterCard not found");
        }
        characterRepository.deleteById(id);
    }

    @Override
    public List<Character> findByVersion(String version) {
        return characterRepository.findByVersion(version);
    }

    @Override
    public List<CharacterResponse> findRandomEnemies(int count) {
        List<Character> enemies = characterRepository.findByType("ENEMY");
        Collections.shuffle(enemies);
        List<CharacterResponse> enemyResponses = enemies.stream()
                .limit(count)
                .map(characterMapping::toCharacterResponse).collect(Collectors.toList());
        return enemyResponses;
    }
}