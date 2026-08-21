package mobile.apis.reader.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateToeicTestRequest {
    @NotBlank(message = "Tên bài thi không được để trống")
    private String testName;

    private String pdfUrl; // Optional if uploaded separately or passed directly

    @NotEmpty(message = "Danh sách câu hỏi không được để trống")
    private List<QuestionItem> questions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionItem {
        private int number;
        private int part;
        private String correctAnswer; // "A", "B", "C", "D"
    }
}
