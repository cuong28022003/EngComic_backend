package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.StartAttemptRequest;
import mobile.businesses.boundaries.reader.StartTestAttemptBoundary;
import mobile.databases.entities.reader.ToeicTestAttemptEntity;
import mobile.databases.entities.reader.ToeicTestEntity;
import mobile.databases.repositories.reader.ToeicTestAttemptRepository;
import mobile.databases.repositories.reader.ToeicTestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StartTestAttemptInteractor implements StartTestAttemptBoundary {

    private final ToeicTestRepository testRepository;
    private final ToeicTestAttemptRepository attemptRepository;
    private final ToeicReaderMapper mapper;

    @Override
    @Transactional
    public Response execute(Request request) {
        ToeicTestEntity test = testRepository.findByIdAndUserId(request.getTestId(), request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài thi"));

        // Lấy số thứ tự lượt làm tiếp theo
        long previousCount = attemptRepository.countByUserIdAndTestId(request.getUserId(), test.getId());
        int attemptNumber = (int) previousCount + 1;

        StartAttemptRequest req = request.getStartData();
        List<Integer> parts = (req != null && req.getSelectedParts() != null && !req.getSelectedParts().isEmpty())
                ? req.getSelectedParts() : Arrays.asList(5, 6, 7);

        String timeMode = (req != null && req.getTimeMode() != null) ? req.getTimeMode() : "full_test";

        ToeicTestAttemptEntity attempt = ToeicTestAttemptEntity.builder()
                .userId(request.getUserId())
                .testId(test.getId())
                .testName(test.getTestName())
                .attemptNumber(attemptNumber)
                .status("in_progress")
                .timeMode(timeMode)
                .selectedParts(parts)
                .part5TargetSeconds(req != null ? req.getPart5TargetSeconds() : 1200)
                .part6TargetSeconds(req != null ? req.getPart6TargetSeconds() : 600)
                .part7TargetSeconds(req != null ? req.getPart7TargetSeconds() : 2700)
                .totalElapsedSeconds(0)
                .part5ElapsedSeconds(0)
                .part6ElapsedSeconds(0)
                .part7ElapsedSeconds(0)
                .startedAt(new Date())
                .lastSavedAt(new Date())
                .build();

        attempt = attemptRepository.save(attempt);

        return Response.builder()
                .data(mapper.toAttemptDto(attempt))
                .build();
    }
}
