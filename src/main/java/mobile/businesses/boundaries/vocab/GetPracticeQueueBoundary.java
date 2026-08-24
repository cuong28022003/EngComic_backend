package mobile.businesses.boundaries.vocab;

import lombok.Builder;
import lombok.Getter;
import mobile.apis.vocab.dtos.PracticeQueueItemDto;

import java.util.List;

public interface GetPracticeQueueBoundary {

    Response execute(Request request);

    @Getter
    @Builder
    class Request {
        private String userId;
        private String deckId;
        private int limit;
    }

    @Getter
    @Builder
    class Response {
        private List<PracticeQueueItemDto> items;
        private int totalDue;
    }
}
