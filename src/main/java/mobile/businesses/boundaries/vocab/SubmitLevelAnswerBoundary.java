package mobile.businesses.boundaries.vocab;

import lombok.Builder;
import lombok.Getter;
import mobile.apis.vocab.dtos.SubmitLevelAnswerRequest;
import mobile.apis.vocab.dtos.SubmitLevelAnswerResponseDto;

public interface SubmitLevelAnswerBoundary {

    Response execute(Request request);

    @Getter
    @Builder
    class Request {
        private String userId;
        private String cardId;
        private SubmitLevelAnswerRequest payload;
    }

    @Getter
    @Builder
    class Response {
        private SubmitLevelAnswerResponseDto data;
    }
}
