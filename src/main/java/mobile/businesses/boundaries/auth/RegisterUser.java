package mobile.businesses.boundaries.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.auth.dtos.RegisterRequestDto;

public interface RegisterUser {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private RegisterRequestDto payload;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private String email;
        private String message;
    }

    Response execute(Request request);
}
