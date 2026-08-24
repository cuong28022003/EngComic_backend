package mobile.businesses.boundaries.vocab;

import lombok.Builder;
import lombok.Getter;
import mobile.apis.vocab.dtos.CardResponseDto;

public interface ClearLeechStatusBoundary {

    Response execute(Request request);

    @Getter
    @Builder
    class Request {
        private String userId;
        private String cardId;
        private String memoryTip;
    }

    @Getter
    @Builder
    class Response {
        private CardResponseDto card;
        private String message;
    }
}
