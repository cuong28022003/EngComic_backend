package mobile.businesses.boundaries.reader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.reader.dtos.ToeicMistakeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetToeicMistakesBoundary {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String userId;
        private String status; // "pending", "explained", "resolved" or null/empty for all
        private Pageable pageable;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private Page<ToeicMistakeDto> data;
    }
}
