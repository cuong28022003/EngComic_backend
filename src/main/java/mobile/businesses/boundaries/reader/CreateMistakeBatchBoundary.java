package mobile.businesses.boundaries.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.reader.dtos.CreateMistakeBatchRequest;
import mobile.apis.reader.dtos.ToeicMistakeDto;

import java.util.List;

public interface CreateMistakeBatchBoundary {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String userId;
        private CreateMistakeBatchRequest batchData;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private List<ToeicMistakeDto> data;
    }
}
