package mobile.businesses.boundaries.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.reader.dtos.SubmitToeicSessionRequest;
import mobile.apis.reader.dtos.SubmitToeicSessionResponse;

public interface SubmitToeicSessionBoundary {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String userId;
        private String testId;
        private SubmitToeicSessionRequest submissionData;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private SubmitToeicSessionResponse data;
    }
}
