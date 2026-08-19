package mobile.apis.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponseDto {
    private String id;
    private String userId;
    private int xp;
    private int diamond;
    private String rankName;
    private int currentStreak;
    private int longestStreak;
    private LocalDate lastStudyDate;
    private boolean isReceivedSeasonReward;
    private boolean isPremium;
    private LocalDateTime premiumExpiredAt;
}

