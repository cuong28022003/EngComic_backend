package mobile.Service.Impl;

import mobile.Service.CharacterService;
import mobile.Service.CloudinaryService;
import mobile.model.Entity.Character;
import mobile.repository.CharacterRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CharacterServiceImpl implements CharacterService {

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public List<Character> findAll() {
        return characterRepository.findAll();
    }

    @Override
    public Character findById(ObjectId id) {
        Optional<Character> characterCard = characterRepository.findById(id);
        return characterCard.orElseThrow(() -> new RuntimeException("CharacterCard not found"));
    }

    @Override
    public Character create(String name, String description, String rarity, MultipartFile image, ObjectId packId, int bonusXp, int bonusDiamond, Map<String, Integer> skillsUsagePerDay) {
        Character character = new Character();
        character.setName(name);
        character.setDescription(description);
        character.setRarity(rarity);
        character.setPackId(packId);
        character.setBonusXp(bonusXp);
        character.setBonusDiamond(bonusDiamond);
        character.setSkillsUsagePerDay(skillsUsagePerDay);
        try {
            String imageUrl = cloudinaryService.uploadFile(image);
            character.setImageUrl(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
        return characterRepository.save(character);
    }

    @Override
    public Character update(ObjectId id, String name, String description, String rarity, MultipartFile image, int bonusXp, int bonusDiamond, Map<String, Integer> skillsUsagePerDay) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CharacterCard not found"));

        character.setName(name);
        character.setDescription(description);
        character.setRarity(rarity);
        character.setBonusXp(bonusXp);
        character.setBonusDiamond(bonusDiamond);
        character.setSkillsUsagePerDay(skillsUsagePerDay);
        try {
            String imageUrl = cloudinaryService.uploadFile(image);
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
}