package mobile.databases.entities.gacha;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RestResource(exported = false)
@Document(collection = "character")
public class CharacterEntity {

    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String name;
    private String rarity; // C, R, SR, SSR
    private String imageUrl;
    private String description;
    private String packId;
    @Builder.Default
    private int bonusXp = 0;
    @Builder.Default
    private int bonusDiamond = 0;
    private Map<String, Integer> skillsUsagePerDay;
    private String version;
    private String type;
    private String transformationCharacterId;
}

