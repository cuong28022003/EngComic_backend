package mobile.apis.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankResponseDto {

    private String id;
    private String name;
    private int minXp;
    private int maxXp;
    private String badge;
    private int rewardDiamond;
    private String rewardCharacterId;
}
