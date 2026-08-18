package mobile.businesses.boundaries.gacha;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.gacha.dtos.CharacterResponseDto;

public interface RollGacha {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String userId;
        private String packId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private CharacterResponseDto character;
        private boolean isNew;
    }

    Response execute(Request request);
}
