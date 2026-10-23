package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.RankResponseDto;
import mobile.businesses.boundaries.user.GetRanks;
import mobile.databases.repositories.user.RankRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRanksInteractor implements GetRanks {

    private final RankRepository rankRepository;

    @Override
    public Response execute() {
        List<RankResponseDto> ranks = rankRepository.findAll(Sort.by(Sort.Direction.ASC, "minXp")).stream()
                .map(rank -> RankResponseDto.builder()
                        .id(rank.getId())
                        .name(rank.getName())
                        .minXp(rank.getMinXp())
                        .maxXp(rank.getMaxXp())
                        .badge(rank.getBadge())
                        .rewardDiamond(rank.getRewardDiamond())
                        .rewardCharacterId(rank.getRewardCharacterId())
                        .build())
                .toList();

        return Response.builder().ranks(ranks).build();
    }
}
