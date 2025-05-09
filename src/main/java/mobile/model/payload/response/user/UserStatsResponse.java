package mobile.model.payload.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {
    private String id;
    private String username;
    private String email;
    private String avatar;
    private int xp;
    private int currentStreak;
    private int longestStreak;
    private String rankName;
    private String rankImage;
    private LocalDate lastStudyDate;
}
