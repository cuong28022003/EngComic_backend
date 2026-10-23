package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.UserStatsResponseDto;
import mobile.businesses.boundaries.user.GetUserStats;
import mobile.databases.entities.user.RankEntity;
import mobile.databases.entities.user.UserStatsEntity;
import mobile.databases.repositories.user.RankRepository;
import mobile.databases.repositories.user.UserStatsRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserStatsInteractor implements GetUserStats {

    private final UserStatsRepository userStatsRepository;
    private final RankRepository rankRepository;

    @Override
    public Response execute(Request request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        UserStatsEntity stats = userStatsRepository.findByUserId(request.getUserId())
                .orElseGet(() -> userStatsRepository.save(UserStatsEntity.builder()
                        .userId(request.getUserId())
                        .build()));

        LocalDate today = LocalDate.now();
        boolean studiedToday = stats.getLastStudyDate() != null && stats.getLastStudyDate().equals(today);

        // If user missed yesterday and today, streak resets to 0
        if (stats.getLastStudyDate() != null && stats.getLastStudyDate().isBefore(today.minusDays(1)) && stats.getCurrentStreak() > 0) {
            stats.setCurrentStreak(0);
            stats = userStatsRepository.save(stats);
        }

        String rankName = resolveRankName(stats);

        UserStatsResponseDto dto = UserStatsResponseDto.builder()
                .id(stats.getId())
                .userId(stats.getUserId())
                .xp(stats.getXp())
                .diamond(stats.getDiamond())
                .rankName(rankName)
                .currentStreak(stats.getCurrentStreak())
                .longestStreak(stats.getLongestStreak())
                .lastStudyDate(stats.getLastStudyDate())
                .studiedToday(studiedToday)
                .isReceivedSeasonReward(stats.isReceivedSeasonReward())
                .isPremium(stats.isPremium())
                .premiumExpiredAt(stats.getPremiumExpiredAt())
                .build();

        return Response.builder()
                .stats(dto)
                .build();
    }

    /**
     * Luôn suy ra tên hạng từ collection "rank" theo XP hiện tại,
     * tránh dùng bản sao rank cũ nhúng trong user_stats.
     */
    private String resolveRankName(UserStatsEntity stats) {
        List<RankEntity> ranks = rankRepository.findAll(Sort.by(Sort.Direction.ASC, "minXp"));
        RankEntity matched = null;
        for (RankEntity rank : ranks) {
            if (stats.getXp() >= rank.getMinXp()) {
                matched = rank;
            }
        }
        if (matched != null && matched.getName() != null) {
            return matched.getName();
        }
        return stats.getRank() != null && stats.getRank().getName() != null
                ? stats.getRank().getName()
                : "Trainee";
    }
}

