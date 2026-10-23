package mobile.apis.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDto {

    private int rank;
    private String userId;
    private String fullName;
    private String username;
    private String imageUrl;
    private int xp;
    private int diamond;
    private String rankName;
    private String rankBadge;
    private int currentStreak;
    private int longestStreak;
    private boolean premium;
    private boolean studiedToday;
}
