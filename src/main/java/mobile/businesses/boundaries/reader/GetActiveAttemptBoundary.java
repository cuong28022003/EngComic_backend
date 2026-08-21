package mobile.businesses.boundaries.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.reader.dtos.ToeicAttemptDto;

public interface GetActiveAttemptBoundary {
    Response execute(Request request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String userId;
        private String testId; // Optional: if provided, check active attempt for this specific test
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private ToeicAttemptDto data; // Null if no active attempt
    }
}
