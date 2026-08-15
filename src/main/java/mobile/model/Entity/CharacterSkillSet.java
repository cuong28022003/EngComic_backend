package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "character_skill")
public class CharacterSkillSet {
    @Id
    private String id;

    private String characterId;

    private List<CharacterSkill> skills;

}
