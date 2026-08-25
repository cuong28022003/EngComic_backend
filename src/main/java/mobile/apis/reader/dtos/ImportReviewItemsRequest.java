package mobile.apis.reader.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportReviewItemsRequest {
    private List<ReviewItemInput> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewItemInput {
        @JsonProperty("question_number")
        @JsonAlias({"questionNumber", "question_no", "question"})
        private int questionNumber;

        @JsonProperty("part")
        private Integer part;

        @JsonProperty("error_type")
        @JsonAlias({"errorType", "type"})
        private String errorType;

        @JsonProperty("error_subtype")
        @JsonAlias({"errorSubtype", "subtype"})
        private String errorSubtype;

        @JsonProperty("related_grammar_topic")
        @JsonAlias({"relatedGrammarTopic", "grammar_topic", "grammarTopic"})
        private String relatedGrammarTopic;

        @JsonProperty("passage_excerpt")
        @JsonAlias({"passageExcerpt", "excerpt", "passage"})
        private String passageExcerpt;

        @JsonProperty("question_text")
        @JsonAlias({"questionText", "question"})
        private String questionText;

        @JsonProperty("options")
        private Map<String, String> options;

        @JsonProperty("explanation")
        private String explanation;

        @JsonProperty("tip")
        private String tip;

        @JsonProperty("key_vocab")
        @JsonAlias({"keyVocab", "vocab", "vocabulary"})
        private List<KeyVocabInput> keyVocab;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyVocabInput {
        @JsonProperty("word")
        private String word;

        @JsonProperty("meaning_vi")
        @JsonAlias({"meaningVi", "meaning", "translation"})
        private String meaningVi;
    }
}
