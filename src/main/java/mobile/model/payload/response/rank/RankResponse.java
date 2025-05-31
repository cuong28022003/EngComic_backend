package mobile.model.payload.response.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.model.payload.response.character.CharacterResponse;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RankResponse {
    private String id;
    private String name;
    private int minXp;
    private int maxXp;
    private String badge;
    private int rewardDiamond;
    private CharacterResponse rewardCharacter;
}
