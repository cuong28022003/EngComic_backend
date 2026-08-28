package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.UserStatsResponseDto;
import mobile.businesses.boundaries.user.EquipPrestigeItem;
import mobile.databases.entities.user.UserLearningStatsEntity;
import mobile.databases.repositories.user.UserLearningStatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EquipPrestigeItemInteractor implements EquipPrestigeItem {

    private final UserLearningStatsRepository userLearningStatsRepository;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        UserLearningStatsEntity stats = userLearningStatsRepository.findByUserId(userId)
                .orElseGet(() -> userLearningStatsRepository.save(UserLearningStatsEntity.builder()
                        .userId(userId)
                        .currentStreak(0)
                        .longestStreak(0)
                        .lastStudyDate(LocalDate.now().minusDays(2))
                        .createdAt(LocalDateTime.now())
                        .build()));

        String itemType = request.getItemType();
        String itemId = request.getItemId();

        if ("title".equalsIgnoreCase(itemType)) {
            stats.setEquippedTitle(itemId);
        } else if ("frame".equalsIgnoreCase(itemType)) {
            stats.setEquippedAvatarFrame(itemId);
        } else {
            throw new IllegalArgumentException("Loại vật phẩm không hợp lệ (title / frame)");
        }

        stats.setUpdatedAt(LocalDateTime.now());
        UserLearningStatsEntity saved = userLearningStatsRepository.save(stats);

        LocalDate today = LocalDate.now();
        boolean studiedToday = saved.getLastStudyDate() != null && saved.getLastStudyDate().equals(today);

        UserStatsResponseDto dto = UserStatsResponseDto.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .xp(saved.getXp())
                .diamond(saved.getDiamond())
                .rankName(saved.getRank() != null ? saved.getRank().getName() : "ĐỒNG (Bronze)")
                .currentStreak(saved.getCurrentStreak())
                .longestStreak(saved.getLongestStreak())
                .lastStudyDate(saved.getLastStudyDate())
                .studiedToday(studiedToday)
                .equippedTitle(saved.getEquippedTitle())
                .equippedAvatarFrame(saved.getEquippedAvatarFrame())
                .unlockedTitles(saved.getUnlockedTitles())
                .unlockedAvatarFrames(saved.getUnlockedAvatarFrames())
                .build();

        return Response.builder()
                .stats(dto)
                .success(true)
                .message("Trang bị thành công!")
                .build();
    }
}
