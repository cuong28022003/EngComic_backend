package mobile.businesses.boundaries.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface CheckEmail {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private boolean valid;
    }

    Response execute(Request request);
}
