package mobile.model.payload.response.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.model.Entity.Pack;
import org.bson.types.ObjectId;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CharacterResponse {
    private String id;
    private String name;
    private String rarity;
    private String imageUrl;
    private String description;
    private Pack pack;
    private int bonusXp;
    private int bonusDiamond;
    private String version; // phiên bản của thẻ, dùng để quản lý các thay đổi trong tương lai

    private Map<String, Integer> skillsUsagePerDay; // {"DOUBLE_XP": 1, "SHOW_ANSWER": 2}
}
