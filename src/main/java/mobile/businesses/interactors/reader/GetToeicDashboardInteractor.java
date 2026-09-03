package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.ToeicDashboardDto;
import mobile.apis.reader.dtos.ToeicTestSummaryDto;
import mobile.businesses.boundaries.reader.GetToeicDashboardBoundary;
import mobile.databases.entities.reader.ToeicTestEntity;
import mobile.databases.repositories.reader.ToeicTestAttemptRepository;
import mobile.databases.repositories.reader.ToeicTestRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetToeicDashboardInteractor implements GetToeicDashboardBoundary {

    private final ToeicTestRepository testRepository;
    private final ToeicTestAttemptRepository attemptRepository;
    private final ToeicReaderMapper mapper;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();

        long totalTests = testRepository.countByUserId(userId);
        long completedTests = testRepository.countByUserIdAndStatus(userId, "completed");
        long totalAttempts = attemptRepository.countByUserId(userId);

        List<ToeicTestEntity> recentList = testRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 10)).getContent();
        List<ToeicTestSummaryDto> recentSummaries = recentList.stream()
                .map(mapper::toSummaryDto)
                .collect(Collectors.toList());

        // Calculate average accuracy
        double totalAccuracy = 0;
        int scoredCount = 0;
        for (ToeicTestEntity t : recentList) {
            if (t.getRawScore() != null && t.getQuestions() != null && !t.getQuestions().isEmpty()) {
                totalAccuracy += ((double) t.getRawScore() / t.getQuestions().size()) * 100.0;
                scoredCount++;
            }
        }
        Double avgAcc = scoredCount > 0 ? Math.round((totalAccuracy / scoredCount) * 10.0) / 10.0 : null;

        ToeicDashboardDto dto = ToeicDashboardDto.builder()
                .totalTests(totalTests)
                .completedTests(completedTests)
                .totalAttempts(totalAttempts)
                .averageAccuracy(avgAcc)
                .recentTests(recentSummaries)
                .build();

        return Response.builder()
                .data(dto)
                .build();
    }
}
