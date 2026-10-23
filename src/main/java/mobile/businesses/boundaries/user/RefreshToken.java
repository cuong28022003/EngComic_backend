package mobile.businesses.boundaries.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface RefreshToken {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String refreshToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private String accessToken;
    }

    Response execute(Request request);
}
