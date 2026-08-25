package mobile.businesses.boundaries.grammar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.grammar.dtos.BatchImportGrammarRequest;
import mobile.apis.grammar.dtos.GrammarPointDto;

import java.util.List;

public interface BatchImportGrammarBoundary {

    Response execute(Request request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Request {
        private List<BatchImportGrammarRequest.GrammarPointInput> items;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class Response {
        private int insertedCount;
        private int updatedCount;
        private int total;
        private List<GrammarPointDto> data;
    }
}
