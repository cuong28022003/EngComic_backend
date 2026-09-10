package mobile.businesses.boundaries.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.vocab.dtos.BatchImportResponseDto;

import java.util.List;

public interface BatchImportCard {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String userId;
        private String jsonContent;
        private String deckId;
        private List<String> promptWords;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private BatchImportResponseDto data;
    }
}

