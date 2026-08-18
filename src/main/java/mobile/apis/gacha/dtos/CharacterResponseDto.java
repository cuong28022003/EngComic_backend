package mobile.apis.gacha.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterResponseDto {
    private String id;
    private String name;
    private String rarity;
    private String imageUrl;
    private String description;
    private String packId;
    private int bonusXp;
    private int bonusDiamond;
    private Map<String, Integer> skillsUsagePerDay;
    private String version;
    private String type;
    private String transformationCharacterId;
}
