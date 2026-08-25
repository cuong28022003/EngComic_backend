package mobile.businesses.boundaries.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.reader.dtos.ToeicTestSummaryDto;
import mobile.apis.reader.dtos.UpdateToeicTestRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UpdateToeicTestBoundary {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String userId;
        private String testId;
        private UpdateToeicTestRequest requestData;
        private MultipartFile pdfFile;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private ToeicTestSummaryDto data;
    }
}
