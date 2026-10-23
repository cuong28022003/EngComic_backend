package mobile.businesses.boundaries.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.user.dtos.LeaderboardEntryDto;
import org.springframework.data.domain.Page;

public interface GetLeaderboard {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private int page;
        private int size;
        private String sort;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private Page<LeaderboardEntryDto> result;
    }

    Response execute(Request request);
}
