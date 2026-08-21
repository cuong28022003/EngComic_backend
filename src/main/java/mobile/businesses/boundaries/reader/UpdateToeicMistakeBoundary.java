package mobile.businesses.boundaries.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.reader.dtos.ToeicMistakeDto;
import mobile.apis.reader.dtos.UpdateMistakeRequest;

public interface UpdateToeicMistakeBoundary {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String userId;
        private String mistakeId;
        private UpdateMistakeRequest updateData;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private ToeicMistakeDto data;
    }
}
