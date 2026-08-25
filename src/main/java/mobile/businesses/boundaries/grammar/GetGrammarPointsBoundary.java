package mobile.businesses.boundaries.grammar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.grammar.dtos.GrammarPointDto;

import java.util.List;

public interface GetGrammarPointsBoundary {

    Response execute(Request request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private String category;
        private String keyword;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private List<GrammarPointDto> data;
        private int total;
    }
}
