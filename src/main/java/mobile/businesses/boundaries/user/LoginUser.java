package mobile.businesses.boundaries.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.user.dtos.AuthResponseDto;
import mobile.apis.user.dtos.LoginRequestDto;

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

