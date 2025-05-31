package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.Service.CharacterService;
import mobile.model.Entity.Rank;
import mobile.model.payload.response.rank.RankResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankMapping {
    private final CharacterService characterService;

    public RankResponse toRankResponse(Rank rank) {
        RankResponse rankResponse = new RankResponse();
        rankResponse.setId(rank.getId().toHexString());
        rankResponse.setName(rank.getName());
        rankResponse.setMinXp(rank.getMinXp());
        rankResponse.setMaxXp(rank.getMaxXp());
        rankResponse.setBadge(rank.getBadge());
        rankResponse.setRewardDiamond(rank.getRewardDiamond());
        rankResponse.setRewardCharacter(characterService.findById(rank.getRewardCharacterId()));
        return rankResponse;
    }
}
