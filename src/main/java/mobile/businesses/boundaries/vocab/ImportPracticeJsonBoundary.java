package mobile.businesses.boundaries.vocab;

import lombok.Builder;
import lombok.Getter;

public interface ImportPracticeJsonBoundary {

    Response execute(Request request);

    @Getter
    @Builder
    class Request {
        private String userId;
        private String deckId;
        private String jsonContent;
    }

    @Getter
    @Builder
    class Response {
        private int totalProcessed;
        private int successCount;
        private String message;
    }
}
