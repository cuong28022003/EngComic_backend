package mobile.apis.reader.dtos;

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
public class CreateMistakeBatchRequest {
    @NotEmpty(message = "Danh sách lỗi không được rỗng")
    private List<Item> mistakes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String testId;
        private String testName;
        private int questionNumber;
        private int part;
        private String userAnswer;
        private String correctAnswer;
    }
}
