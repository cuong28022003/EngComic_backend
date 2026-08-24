package mobile.businesses.boundaries.vocab;

import lombok.Builder;
import lombok.Getter;
import mobile.apis.vocab.dtos.CardResponseDto;

import java.util.List;

public interface GetLeechCardsBoundary {

    Response execute(Request request);

    @Getter
    @Builder
    class Request {
        private String userId;
    }

    @Getter
    @Builder
    class Response {
        private List<CardResponseDto> cards;
    }
}
