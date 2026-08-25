package mobile.apis.grammar.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrUpdateGrammarPointRequest {
    @JsonProperty("topic")
    private String topic;

    @JsonProperty("category")
    private String category;

    @JsonProperty("shortRule")
    private String shortRule;

    @JsonProperty("structure")
    private String structure;

    @JsonProperty("signalWords")
    private List<String> signalWords;

    @JsonProperty("commonMistake")
    private String commonMistake;

    @JsonProperty("examples")
    private List<GrammarExampleInput> examples;

    @JsonProperty("searchKeywords")
    private List<String> searchKeywords;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarExampleInput {
        private String text;
        private String note;
    }
}
