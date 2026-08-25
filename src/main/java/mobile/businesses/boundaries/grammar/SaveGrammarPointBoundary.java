package mobile.businesses.boundaries.grammar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.grammar.dtos.CreateOrUpdateGrammarPointRequest;
import mobile.apis.grammar.dtos.GrammarPointDto;

public interface SaveGrammarPointBoundary {

    Response execute(Request request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String id;
        private CreateOrUpdateGrammarPointRequest data;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private GrammarPointDto data;
    }
}
