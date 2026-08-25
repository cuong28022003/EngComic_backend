package mobile.apis.reader.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateToeicTestRequest {
    private String testName;
    private String pdfUrl;
    private List<CreateToeicTestRequest.QuestionItem> questions;
}
