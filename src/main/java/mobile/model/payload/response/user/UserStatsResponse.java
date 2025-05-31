package mobile.model.payload.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.model.Entity.Rank;
import mobile.model.payload.response.rank.RankResponse;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {
    private String id;
    private String userId;
    private int xp;
    private int diamond;
    private RankResponse rank;
    private int currentStreak;
    private int longestStreak;
    private LocalDate lastStudyDate;
    private boolean isReceivedSeasonReward;
    private boolean isPremium = false;
    private LocalDateTime premiumExpiredAt;
}
