package mobile.model.payload.request.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRankRequest {
    private String name;
    private int minXp;
    private int maxXp;
    private int rewardDiamond;
    private String rewardCharacterId;
}
