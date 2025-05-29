package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported = false)
@Document(collection = "character_usage")
public class CharacterUsage {
    @Id
    private ObjectId id;

    private ObjectId userId;
    private ObjectId characterId;
    private LocalDate date; // YYYY-MM-DD

    // Tên kỹ năng và số lần đã sử dụng trong ngày đó
    private Map<String, Integer> usedSkills;

    public CharacterUsage(ObjectId userId, ObjectId characterId, LocalDate date, Map<String, Integer> usedSkills) {
        this.userId = userId;
        this.characterId = characterId;
        this.date = date;
        this.usedSkills = usedSkills;
    }
}
