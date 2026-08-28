package mobile.businesses.boundaries.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.user.dtos.UserStatsResponseDto;

public interface EquipPrestigeItem {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String userId;
        private String itemType; // "title" | "frame"
        private String itemId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private UserStatsResponseDto stats;
        private boolean success;
        private String message;
    }
}
