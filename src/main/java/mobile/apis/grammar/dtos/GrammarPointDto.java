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
    private String shortRule;
    private String structure;
    private List<String> signalWords;
    private String commonMistake;
    private List<GrammarExampleDto> examples;
    private List<String> searchKeywords;
    private Date createdAt;
    private Date updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarExampleDto {
        private String text;
        private String note;
    }
}
