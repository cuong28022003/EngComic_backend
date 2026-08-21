package mobile.businesses.boundaries.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.reader.dtos.ImportReviewItemsRequest;
import mobile.apis.reader.dtos.ToeicMistakeDto;

import java.util.List;

public interface ImportMistakeReviewsBoundary {
    Response execute(Request request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String userId;
        private ImportReviewItemsRequest importData;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private List<ToeicMistakeDto> data;
    }
}
