package mobile.Service.Impl;

import mobile.Service.CharacterUsageService;
import mobile.model.Entity.Character;
import mobile.model.Entity.CharacterUsage;
import mobile.repository.CharacterRepository;
import mobile.repository.CharacterUsageRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class CharacterUsageServiceImpl  implements CharacterUsageService {
    @Autowired
    private CharacterUsageRepository usageRepository;

    @Autowired
    private CharacterRepository characterRepository;

    @Override
    public CharacterUsage getOrCreateUsage(ObjectId userId, ObjectId characterId, LocalDate date) {
        return usageRepository
                .findByUserIdAndCharacterIdAndDate(userId, characterId, date)
                .orElseGet(() -> {
                    CharacterUsage usage = new CharacterUsage(userId, characterId, date, new HashMap<>());
                    return usageRepository.save(usage);
                });
    }

    @Override
    public boolean canUseSkill(ObjectId userId, ObjectId characterId, LocalDate date, String skill) {
        // Fetch the Character entity by characterId
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found with id: " + characterId));

        // Get maxUsage for the skill from skillsUsagePerDay
        Integer maxUsage = character.getSkillsUsagePerDay().get(skill);
        if (maxUsage == null) {
            throw new IllegalArgumentException("Skill not found in character's skillsUsagePerDay: " + skill);
        }

        // Check if the skill can be used
        CharacterUsage usage = getOrCreateUsage(userId, characterId, date);
        int used = usage.getUsedSkills().getOrDefault(skill, 0);
        return used < maxUsage;
    }

    @Override
    public void markSkillUsed(ObjectId userId, ObjectId characterId, LocalDate date, String skill) {
        CharacterUsage usage = getOrCreateUsage(userId, characterId, date);

        int currentUsage = usage.getUsedSkills().getOrDefault(skill, 0);

        // Bước 3: Tăng số lần sử dụng kỹ năng lên 1
        int updatedUsage = currentUsage + 1;
        usage.getUsedSkills().put(skill, updatedUsage);

        usageRepository.save(usage);
    }
}
