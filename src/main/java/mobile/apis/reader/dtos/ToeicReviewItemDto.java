package mobile.apis.reader.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToeicReviewItemDto {
    private String id;
    private String testId;
    private String attemptId;
    private int questionNumber;
    private int part;
    private String errorType;
    private String errorSubtype;
    private String passageExcerpt;
    private String questionText;
    private Map<String, String> options;
    private String explanation;
    private String tip;
    private List<KeyVocabDto> keyVocab;
    private Date createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyVocabDto {
        private String word;
        private String meaningVi;
    }
}
