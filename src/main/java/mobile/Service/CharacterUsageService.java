package mobile.Service;

import mobile.model.Entity.CharacterUsage;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CharacterUsageService {
    CharacterUsage getOrCreateUsage(ObjectId userId, String characterId, LocalDate date);
    boolean canUseSkill(ObjectId userId, String characterId, LocalDate date, String skill);
    void markSkillUsed(ObjectId userId, String characterId, LocalDate date, String skill);
}
