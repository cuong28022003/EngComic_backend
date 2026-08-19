package mobile.businesses.boundaries.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.vocab.dtos.CardResponseDto;
import mobile.apis.vocab.dtos.CreateCardRequest;

public interface UpdateCard {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String cardId;
        private CreateCardRequest payload;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private CardResponseDto card;
    }
}

