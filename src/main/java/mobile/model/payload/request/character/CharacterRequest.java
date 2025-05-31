package mobile.model.payload.request.character;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Map;

@Data
public class CharacterRequest {
    private String name;
    private String description;
    private String rarity;
    private String packId;
    private int bonusXp;
    private int bonusDiamond;
    private String version;
    private Map<String, Integer> skillsUsagePerDay;
}
