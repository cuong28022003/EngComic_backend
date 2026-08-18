package mobile.businesses.boundaries.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.auth.dtos.AuthResponseDto;
import mobile.apis.auth.dtos.LoginRequestDto;

public interface LoginUser {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private LoginRequestDto payload;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private AuthResponseDto data;
    }

    Response execute(Request request);
}
