package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.UserStatsResponseDto;
import mobile.businesses.boundaries.user.GetLearningStats;
import mobile.databases.entities.user.UserLearningStatsEntity;
import mobile.databases.repositories.user.UserLearningStatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GetLearningStatsInteractor implements GetLearningStats {

    private final UserLearningStatsRepository userLearningStatsRepository;

    @Override
    public Response execute(Request request) {
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        UserLearningStatsEntity stats = userLearningStatsRepository.findByUserId(request.getUserId())
                .orElseGet(() -> userLearningStatsRepository.save(UserLearningStatsEntity.builder()
                        .userId(request.getUserId())
                        .currentStreak(0)
                        .longestStreak(0)
                        .lastStudyDate(LocalDate.now().minusDays(2))
                        .createdAt(LocalDateTime.now())
                        .build()));

        LocalDate today = LocalDate.now();
        boolean studiedToday = stats.getLastStudyDate() != null && stats.getLastStudyDate().equals(today);

        // If user missed yesterday and today, streak resets to 0
        if (stats.getLastStudyDate() != null && stats.getLastStudyDate().isBefore(today.minusDays(1)) && stats.getCurrentStreak() > 0) {
            stats.setCurrentStreak(0);
            stats = userLearningStatsRepository.save(stats);
        }

        // Ensure defaults if not set
        if (stats.getUnlockedTitles() == null || stats.getUnlockedTitles().isEmpty()) {
            stats.setUnlockedTitles(new java.util.ArrayList<>(java.util.List.of("title_rookie")));
        }
        if (stats.getUnlockedAvatarFrames() == null || stats.getUnlockedAvatarFrames().isEmpty()) {
            stats.setUnlockedAvatarFrames(new java.util.ArrayList<>(java.util.List.of("frame_default")));
        }
        if (stats.getEquippedTitle() == null || stats.getEquippedTitle().isBlank()) {
            stats.setEquippedTitle("title_rookie");
        }
        if (stats.getEquippedAvatarFrame() == null || stats.getEquippedAvatarFrame().isBlank()) {
            stats.setEquippedAvatarFrame("frame_default");
        }

        UserStatsResponseDto dto = UserStatsResponseDto.builder()
                .id(stats.getId())
                .userId(stats.getUserId())
                .xp(stats.getXp())
                .diamond(stats.getDiamond())
                .rankName(stats.getRank() != null ? stats.getRank().getName() : "ĐỒNG (Bronze)")
                .currentStreak(stats.getCurrentStreak())
                .longestStreak(stats.getLongestStreak())
                .lastStudyDate(stats.getLastStudyDate())
                .studiedToday(studiedToday)
                .equippedTitle(stats.getEquippedTitle())
                .equippedAvatarFrame(stats.getEquippedAvatarFrame())
                .unlockedTitles(stats.getUnlockedTitles())
                .unlockedAvatarFrames(stats.getUnlockedAvatarFrames())
                .build();

        return Response.builder()
                .stats(dto)
                .build();
    }
}
