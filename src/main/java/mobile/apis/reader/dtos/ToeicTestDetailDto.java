package mobile.apis.reader.dtos;

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
public class ToeicTestDetailDto {
    private String id;
    private String testName;
    private String pdfUrl;
    private String status;
    private Integer rawScore;
    private Integer scaledScore;
    private List<QuestionDetailItem> questions;
    private Date createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDetailItem {
        private int number;
        private int part;
        private String correctAnswer;
    }
}
