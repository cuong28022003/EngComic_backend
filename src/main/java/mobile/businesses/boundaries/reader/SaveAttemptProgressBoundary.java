package mobile.businesses.boundaries.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.reader.dtos.SaveProgressRequest;
import mobile.apis.reader.dtos.ToeicAttemptDto;

public interface SaveAttemptProgressBoundary {
    Response execute(Request request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String userId;
        private String attemptId;
        private SaveProgressRequest progressData;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private ToeicAttemptDto data;
    }
}
