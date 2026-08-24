package mobile.businesses.boundaries.vocab;

import lombok.Builder;
import lombok.Getter;
import mobile.apis.vocab.dtos.PracticePromptResponseDto;

public interface GeneratePracticePromptBoundary {

    Response execute(Request request);

    @Getter
    @Builder
    class Request {
        private String userId;
        private String deckId;
    }

    @Getter
    @Builder
    class Response {
        private PracticePromptResponseDto data;
    }
}
