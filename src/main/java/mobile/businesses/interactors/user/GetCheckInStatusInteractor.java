package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.CheckInResponseDto;
import mobile.businesses.boundaries.user.GetCheckInStatus;
import mobile.databases.entities.user.UserStatsEntity;
import mobile.databases.repositories.user.UserStatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCheckInStatusInteractor implements GetCheckInStatus {

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
        boolean alreadyCheckedIn = today.equals(stats.getLastCheckInDate());

        // Computed display streak: nếu bỏ quá 1 ngày thì chuỗi hiển thị về 0
        int displayStreak = stats.getCurrentStreak();
        if (stats.getLastCheckInDate() != null
                && stats.getLastCheckInDate().isBefore(today.minusDays(1))) {
            displayStreak = 0;
        }

        List<String> history = stats.getCheckInHistory() != null
                ? new ArrayList<>(stats.getCheckInHistory())
                : new ArrayList<>();

        if (request.getYear() != null && request.getMonth() != null) {
            String prefix = String.format("%04d-%02d", request.getYear(), request.getMonth());
            history = history.stream().filter(d -> d.startsWith(prefix)).toList();
        }

        CheckInResponseDto dto = CheckInResponseDto.builder()
                .alreadyCheckedIn(alreadyCheckedIn)
                .streak(displayStreak)
                .longestStreak(stats.getLongestStreak())
                .totalCheckIns(stats.getTotalCheckIns())
                .lastCheckInDate(stats.getLastCheckInDate())
                .rewardDiamonds(0)
                .rewardXp(0)
                .message(alreadyCheckedIn ? "Bạn đã điểm danh hôm nay." : "Hôm nay bạn chưa điểm danh.")
                .checkInHistory(history)
                .build();

        return Response.builder()
                .result(dto)
                .build();
    }
}
