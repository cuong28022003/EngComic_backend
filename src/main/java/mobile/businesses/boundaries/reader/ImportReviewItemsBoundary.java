package mobile.businesses.boundaries.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.reader.dtos.ImportReviewItemsRequest;
import mobile.apis.reader.dtos.ToeicReviewItemDto;

import java.util.List;

public interface ImportReviewItemsBoundary {
    Response execute(Request request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String userId;
        private String attemptId;
        private ImportReviewItemsRequest importData;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private List<ToeicReviewItemDto> data;
    }
}
