package mobile.businesses.interactors.userstats;

import lombok.RequiredArgsConstructor;
import mobile.apis.userstats.dtos.UserStatsResponseDto;
import mobile.businesses.boundaries.userstats.GetUserStats;
import mobile.databases.entities.userstats.UserStatsEntity;
import mobile.databases.repositories.userstats.UserStatsRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserStatsInteractor implements GetUserStats {

    private final UserStatsRepository userStatsRepository;

    @Override
    public Response execute(Request request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        UserStatsEntity stats = userStatsRepository.findByUserId(request.getUserId())
                .orElseGet(() -> userStatsRepository.save(UserStatsEntity.builder()
                        .userId(request.getUserId())
                        .build()));

        UserStatsResponseDto dto = UserStatsResponseDto.builder()
                .id(stats.getId())
                .userId(stats.getUserId())
                .xp(stats.getXp())
                .diamond(stats.getDiamond())
                .rankName(stats.getRank() != null ? stats.getRank().getName() : "BRONZE")
                .currentStreak(stats.getCurrentStreak())
                .longestStreak(stats.getLongestStreak())
                .lastStudyDate(stats.getLastStudyDate())
                .isReceivedSeasonReward(stats.isReceivedSeasonReward())
                .isPremium(stats.isPremium())
                .premiumExpiredAt(stats.getPremiumExpiredAt())
                .build();

        return Response.builder()
                .stats(dto)
                .build();
    }
}
