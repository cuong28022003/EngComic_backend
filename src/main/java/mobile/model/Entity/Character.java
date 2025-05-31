package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Id;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported=false)
@Document(collection = "character")
public class Character {
    @Id
    private ObjectId id;
    private String name;
    private String rarity; // COMMON, RARE, SSR...
    private String imageUrl;
    private String description;
    private ObjectId packId; // ID của gói thẻ mà thẻ này thuộc về
    private int bonusXp;
    private int bonusDiamond;

    private Map<String, Integer> skillsUsagePerDay; // {"DOUBLE_XP": 1, "SHOW_ANSWER": 2}

    private String version;
}
