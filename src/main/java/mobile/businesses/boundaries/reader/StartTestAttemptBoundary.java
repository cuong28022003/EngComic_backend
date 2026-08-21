package mobile.businesses.boundaries.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.reader.dtos.StartAttemptRequest;
import mobile.apis.reader.dtos.ToeicAttemptDto;

public interface StartTestAttemptBoundary {
    Response execute(Request request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String userId;
        private String testId;
        private StartAttemptRequest startData;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private ToeicAttemptDto data;
    }
}
