package mobile.businesses.boundaries.userstats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.userstats.dtos.UserStatsResponseDto;

public interface GetUserStats {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String userId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private UserStatsResponseDto stats;
    }

    Response execute(Request request);
}
