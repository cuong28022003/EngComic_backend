package mobile.businesses.boundaries.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.user.dtos.UserStatsResponseDto;

public interface RecordStudyActivity {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String userId;
        private int xpEarned;
        private String activityType; // "practice", "reading", "test", "checkin"
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private UserStatsResponseDto stats;
        private boolean streakIncreased;
        private String message;
    }
}
