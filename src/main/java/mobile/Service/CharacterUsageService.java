package mobile.Service;

import mobile.model.Entity.CharacterUsage;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CharacterUsageService {
    CharacterUsage getOrCreateUsage(ObjectId userId, ObjectId characterId, LocalDate date);
    boolean canUseSkill(ObjectId userId, ObjectId characterId, LocalDate date, String skill);
    void markSkillUsed(ObjectId userId, ObjectId characterId, LocalDate date, String skill);
}
