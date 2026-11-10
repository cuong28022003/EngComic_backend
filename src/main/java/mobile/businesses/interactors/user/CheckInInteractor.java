package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.CheckInResponseDto;
import mobile.businesses.boundaries.user.CheckIn;
import mobile.databases.entities.user.UserStatsEntity;
import mobile.databases.repositories.user.UserStatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckInInteractor implements CheckIn {

    private static final int XP_PER_CHECK_IN = 20;
    private static final int HISTORY_LIMIT = 120;

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
                        .build()));

        LocalDate today = LocalDate.now();
        List<String> history = new ArrayList<>(
                stats.getCheckInHistory() != null ? stats.getCheckInHistory() : List.of());

        // Đã điểm danh hôm nay → không cộng lại
        if (today.equals(stats.getLastCheckInDate())) {
            return Response.builder()
                    .result(buildDto(stats, true, 0, 0,
                            String.format("Bạn đã điểm danh hôm nay (Chuỗi %d ngày).", stats.getCurrentStreak()),
                            history))
                    .build();
        }

        boolean isConsecutive = today.minusDays(1).equals(stats.getLastCheckInDate());
        int nextStreak = isConsecutive ? stats.getCurrentStreak() + 1 : 1;

        stats.setCurrentStreak(nextStreak);
        stats.setLongestStreak(Math.max(stats.getLongestStreak(), nextStreak));
        stats.setLastCheckInDate(today);
        stats.setTotalCheckIns(stats.getTotalCheckIns() + 1);

        if (!history.contains(today.toString())) {
            history.add(today.toString());
        }
        if (history.size() > HISTORY_LIMIT) {
            history = new ArrayList<>(history.subList(history.size() - HISTORY_LIMIT, history.size()));
        }
        stats.setCheckInHistory(history);

        int rewardDiamonds = rewardFor(nextStreak);
        stats.setDiamond(stats.getDiamond() + rewardDiamonds);
        stats.setXp(stats.getXp() + XP_PER_CHECK_IN);

        UserStatsEntity saved = userStatsRepository.save(stats);

        String message;
        if (rewardDiamonds > 0) {
            message = String.format("Điểm danh thành công! Chuỗi %d ngày — nhận thưởng +%d kim cương.",
                    nextStreak, rewardDiamonds);
        } else {
            message = String.format("Điểm danh thành công! Chuỗi %d ngày liên tiếp.", nextStreak);
        }

        return Response.builder()
                .result(buildDto(saved, false, rewardDiamonds, XP_PER_CHECK_IN, message, history))
                .build();
    }

    private int rewardFor(int streak) {
        return switch (streak) {
            case 1 -> 5;
            case 3 -> 10;
            case 7 -> 30;
            case 14 -> 60;
            case 30 -> 150;
            default -> 0;
        };
    }

    private CheckInResponseDto buildDto(UserStatsEntity stats, boolean already, int rewardDiamonds,
                                        int rewardXp, String message, List<String> history) {
        return CheckInResponseDto.builder()
                .alreadyCheckedIn(already)
                .streak(stats.getCurrentStreak())
                .longestStreak(stats.getLongestStreak())
                .totalCheckIns(stats.getTotalCheckIns())
                .lastCheckInDate(stats.getLastCheckInDate())
                .rewardDiamonds(rewardDiamonds)
                .rewardXp(rewardXp)
                .message(message)
                .checkInHistory(history)
                .build();
    }
}
