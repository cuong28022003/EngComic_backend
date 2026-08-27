package mobile.apis.grammar.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrammarPointDto {
    private String id;
    private String topic;
    private String category;
    private String level;
    private String summary;
    private String shortRule;
    private String structure;
    private List<String> signalWords;
    private String commonMistake;
    private List<GrammarExampleDto> examples;
    private List<GrammarUsageDto> usages;
    private List<String> commonMistakes;
    private List<String> examTips;
    private List<GrammarComparisonDto> comparisons;
    private List<String> searchKeywords;
    private Date createdAt;
    private Date updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarUsageDto {
        private String title;
        private String structure;
        private String explanation;
        private List<String> signalWords;
        private List<GrammarExampleDto> examples;
        private String note;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarExampleDto {
        private String text;
        private String translation;
        private String highlight;
        private String note;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarComparisonDto {
        private String compareWith;
        private String coreDifference;
        private String currentExample;
        private String targetExample;
    }
}
