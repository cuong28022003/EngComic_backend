package mobile.apis.grammar.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class BatchImportGrammarRequest {

    private List<GrammarPointInput> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarPointInput {
        @JsonProperty("topic")
        private String topic;

        @JsonProperty("category")
        private String category;

        @JsonProperty("short_rule")
        @JsonAlias({"shortRule", "rule", "summary"})
        private String shortRule;

        @JsonProperty("structure")
        @JsonAlias({"formula", "form"})
        private String structure;

        @JsonProperty("signal_words")
        @JsonAlias({"signalWords", "signals", "keywords"})
        private List<String> signalWords;

        @JsonProperty("common_mistake")
        @JsonAlias({"commonMistake", "mistake", "pitfall"})
        private String commonMistake;

        @JsonProperty("examples")
        private List<GrammarExampleInput> examples;

        @JsonProperty("search_keywords")
        @JsonAlias({"searchKeywords", "tags"})
        private List<String> searchKeywords;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarExampleInput {
        @JsonProperty("text")
        private String text;

        @JsonProperty("note")
        private String note;
    }
}
