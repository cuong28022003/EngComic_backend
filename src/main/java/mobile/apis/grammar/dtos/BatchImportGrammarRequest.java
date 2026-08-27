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

        @JsonProperty("level")
        private String level;

        @JsonProperty("summary")
        @JsonAlias({"short_rule", "shortRule", "rule", "overview"})
        private String summary;

        @JsonProperty("short_rule")
        @JsonAlias({"shortRule"})
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

        @JsonProperty("usages")
        private List<GrammarUsageInput> usages;

        @JsonProperty("common_mistakes")
        @JsonAlias({"commonMistakes", "mistakes", "pitfalls"})
        private List<String> commonMistakes;

        @JsonProperty("exam_tips")
        @JsonAlias({"examTips", "tips", "hacks"})
        private List<String> examTips;

        @JsonProperty("comparisons")
        private List<GrammarComparisonInput> comparisons;

        @JsonProperty("search_keywords")
        @JsonAlias({"searchKeywords", "tags"})
        private List<String> searchKeywords;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarUsageInput {
        @JsonProperty("title")
        @JsonAlias({"name", "usage"})
        private String title;

        @JsonProperty("structure")
        @JsonAlias({"formula", "form"})
        private String structure;

        @JsonProperty("explanation")
        @JsonAlias({"desc", "description", "meaning"})
        private String explanation;

        @JsonProperty("signal_words")
        @JsonAlias({"signalWords", "signals"})
        private List<String> signalWords;

        @JsonProperty("examples")
        private List<GrammarExampleInput> examples;

        @JsonProperty("note")
        private String note;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarExampleInput {
        @JsonProperty("text")
        @JsonAlias({"sentence", "en"})
        private String text;

        @JsonProperty("translation")
        @JsonAlias({"meaning", "vi", "trans"})
        private String translation;

        @JsonProperty("highlight")
        private String highlight;

        @JsonProperty("note")
        @JsonAlias({"explanation"})
        private String note;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarComparisonInput {
        @JsonProperty("compare_with")
        @JsonAlias({"compareWith", "target", "contrast_with"})
        private String compareWith;

        @JsonProperty("core_difference")
        @JsonAlias({"coreDifference", "difference", "diff"})
        private String coreDifference;

        @JsonProperty("current_example")
        @JsonAlias({"currentExample", "ex1"})
        private String currentExample;

        @JsonProperty("target_example")
        @JsonAlias({"targetExample", "ex2"})
        private String targetExample;
    }
}
