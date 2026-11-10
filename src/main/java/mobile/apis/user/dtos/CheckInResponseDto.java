package mobile.apis.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponseDto {
    private boolean alreadyCheckedIn;
    private int streak;
    private int longestStreak;
    private int totalCheckIns;
    private LocalDate lastCheckInDate;
    private int rewardDiamonds;
    private int rewardXp;
    private String message;

    @Builder.Default
    private List<String> checkInHistory = new ArrayList<>();
}
