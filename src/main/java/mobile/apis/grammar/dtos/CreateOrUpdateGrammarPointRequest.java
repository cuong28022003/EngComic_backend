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
public class CreateOrUpdateGrammarPointRequest {
    @JsonProperty("topic")
    private String topic;

    @JsonProperty("category")
    private String category;

    @JsonProperty("level")
    private String level;

    @JsonProperty("summary")
    @JsonAlias({"shortRule", "short_rule", "rule"})
    private String summary;

    @JsonProperty("shortRule")
    @JsonAlias({"short_rule"})
    private String shortRule;

    @JsonProperty("structure")
    @JsonAlias({"formula", "form"})
    private String structure;

    @JsonProperty("signalWords")
    @JsonAlias({"signal_words", "signals"})
    private List<String> signalWords;

    @JsonProperty("commonMistake")
    @JsonAlias({"common_mistake"})
    private String commonMistake;

    @JsonProperty("examples")
    private List<GrammarExampleInput> examples;

    @JsonProperty("usages")
    private List<GrammarUsageInput> usages;

    @JsonProperty("commonMistakes")
    @JsonAlias({"common_mistakes", "mistakes"})
    private List<String> commonMistakes;

    @JsonProperty("examTips")
    @JsonAlias({"exam_tips", "tips"})
    private List<String> examTips;

    @JsonProperty("comparisons")
    private List<GrammarComparisonInput> comparisons;

    @JsonProperty("searchKeywords")
    @JsonAlias({"search_keywords", "tags"})
    private List<String> searchKeywords;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarUsageInput {
        @JsonProperty("title")
        private String title;

        @JsonProperty("structure")
        @JsonAlias({"formula", "form"})
        private String structure;

        @JsonProperty("explanation")
        @JsonAlias({"desc", "description"})
        private String explanation;

        @JsonProperty("signalWords")
        @JsonAlias({"signal_words", "signals"})
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
        private String text;

        @JsonProperty("translation")
        @JsonAlias({"meaning", "trans"})
        private String translation;

        @JsonProperty("highlight")
        private String highlight;

        @JsonProperty("note")
        private String note;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarComparisonInput {
        @JsonProperty("compareWith")
        @JsonAlias({"compare_with", "target"})
        private String compareWith;

        @JsonProperty("coreDifference")
        @JsonAlias({"core_difference", "difference", "diff"})
        private String coreDifference;

        @JsonProperty("currentExample")
        @JsonAlias({"current_example"})
        private String currentExample;

        @JsonProperty("targetExample")
        @JsonAlias({"target_example"})
        private String targetExample;
    }
}
