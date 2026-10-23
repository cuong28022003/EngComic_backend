package mobile.businesses.boundaries.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.user.dtos.RankResponseDto;

import java.util.List;

public interface GetRanks {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private List<RankResponseDto> ranks;
    }

    Response execute();
}
