package mobile.businesses.boundaries.gacha;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.gacha.dtos.PackResponseDto;

import java.util.List;

public interface GetPacks {

    @Data
    @Builder
    @NoArgsConstructor
    class Request {}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private List<PackResponseDto> packs;
    }

    Response execute(Request request);
}
