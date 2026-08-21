package mobile.apis.reader.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToeicTestSummaryDto {
    private String id;
    private String testName;
    private String pdfUrl;
    private int questionCount;
    private Integer rawScore;
    private Integer scaledScore;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
