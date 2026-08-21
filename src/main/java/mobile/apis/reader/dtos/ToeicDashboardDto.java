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
public class ToeicDashboardDto {
    private long totalTests;
    private long completedTests;
    private long pendingMistakes;
    private Double averageAccuracy;
    private List<ToeicTestSummaryDto> recentTests;
}
