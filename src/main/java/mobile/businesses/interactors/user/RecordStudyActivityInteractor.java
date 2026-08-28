package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.UserStatsResponseDto;
import mobile.businesses.boundaries.user.RecordStudyActivity;
import mobile.databases.entities.user.UserStatsEntity;
import mobile.databases.repositories.user.UserStatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RecordStudyActivityInteractor implements RecordStudyActivity {

    private final UserStatsRepository userStatsRepository;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        UserStatsEntity stats = userStatsRepository.findByUserId(userId)
                .orElseGet(() -> userStatsRepository.save(UserStatsEntity.builder()
                        .userId(userId)
                        .currentStreak(0)
                        .longestStreak(0)
                        .lastStudyDate(LocalDate.now().minusDays(2))
                        .build()));

        LocalDate today = LocalDate.now();
        LocalDate lastDate = stats.getLastStudyDate();
        boolean streakIncreased = false;
        String message;

        if (lastDate == null) {
            stats.setCurrentStreak(1);
            stats.setLongestStreak(Math.max(stats.getLongestStreak(), 1));
            stats.setLastStudyDate(today);
            streakIncreased = true;
            message = "Khởi đầu chuỗi học tập mới: 1 ngày liên tiếp!";
        } else if (lastDate.equals(today)) {
            // Already studied today
            message = String.format("Bạn đã hoàn thành mục tiêu học tập hôm nay (Chuỗi %d ngày)!", stats.getCurrentStreak());
        } else if (lastDate.equals(today.minusDays(1))) {
            // Consecutive day
            int nextStreak = stats.getCurrentStreak() + 1;
            stats.setCurrentStreak(nextStreak);
            stats.setLongestStreak(Math.max(stats.getLongestStreak(), nextStreak));
            stats.setLastStudyDate(today);
            streakIncreased = true;
            // Bonus diamonds for milestones
            if (nextStreak % 7 == 0) {
                stats.setDiamond(stats.getDiamond() + 20);
                message = String.format("Xuất sắc! Chuỗi %d ngày liên tiếp (Nhận thưởng +20 Kim Cương)!", nextStreak);
            } else {
                message = String.format("Tuyệt vời! Đã tăng chuỗi lên %d ngày liên tiếp!", nextStreak);
            }
        } else {
            // Streak broken, restart at 1
            stats.setCurrentStreak(1);
            stats.setLongestStreak(Math.max(stats.getLongestStreak(), 1));
            stats.setLastStudyDate(today);
            streakIncreased = true;
            message = "Khởi đầu chuỗi học tập mới: 1 ngày!";
        }

        // Add XP earned
        int earnedXp = request.getXpEarned() > 0 ? request.getXpEarned() : 10;
        stats.setXp(stats.getXp() + earnedXp);

        UserStatsEntity saved = userStatsRepository.save(stats);

        UserStatsResponseDto dto = UserStatsResponseDto.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .xp(saved.getXp())
                .diamond(saved.getDiamond())
                .rankName(saved.getRank() != null ? saved.getRank().getName() : "BRONZE")
                .currentStreak(saved.getCurrentStreak())
                .longestStreak(saved.getLongestStreak())
                .lastStudyDate(saved.getLastStudyDate())
                .studiedToday(true)
                .isReceivedSeasonReward(saved.isReceivedSeasonReward())
                .isPremium(saved.isPremium())
                .premiumExpiredAt(saved.getPremiumExpiredAt())
                .build();

        return Response.builder()
                .stats(dto)
                .streakIncreased(streakIncreased)
                .message(message)
                .build();
    }
}
