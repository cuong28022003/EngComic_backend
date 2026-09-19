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
        String section = test.getSection() != null ? test.getSection() : "reading";
        boolean isListening = "listening".equals(section);
        List<Integer> defaultParts = isListening ? Arrays.asList(1, 2, 3, 4) : Arrays.asList(5, 6, 7);
        List<Integer> parts = (req != null && req.getSelectedParts() != null && !req.getSelectedParts().isEmpty())
                ? req.getSelectedParts() : defaultParts;

        String timeMode = (req != null && req.getTimeMode() != null) ? req.getTimeMode() : "full_test";

        // Mặc định thời lượng mỗi phần theo section
        int part1Target = req != null ? req.getPart1TargetSeconds() : (isListening ? 300 : 0);
        int part2Target = req != null ? req.getPart2TargetSeconds() : (isListening ? 480 : 0);
        int part3Target = req != null ? req.getPart3TargetSeconds() : (isListening ? 960 : 0);
        int part4Target = req != null ? req.getPart4TargetSeconds() : (isListening ? 960 : 0);
        int part5Target = req != null ? req.getPart5TargetSeconds() : 1200;
        int part6Target = req != null ? req.getPart6TargetSeconds() : 600;
        int part7Target = req != null ? req.getPart7TargetSeconds() : 2700;

        ToeicTestAttemptEntity attempt = ToeicTestAttemptEntity.builder()
                .userId(request.getUserId())
                .testId(test.getId())
                .testName(test.getTestName())
                .attemptNumber(attemptNumber)
                .status("in_progress")
                .timeMode(timeMode)
                .selectedParts(parts)
                .part1TargetSeconds(part1Target)
                .part2TargetSeconds(part2Target)
                .part3TargetSeconds(part3Target)
                .part4TargetSeconds(part4Target)
                .part5TargetSeconds(part5Target)
                .part6TargetSeconds(part6Target)
                .part7TargetSeconds(part7Target)
                .totalElapsedSeconds(0)
                .part1ElapsedSeconds(0)
                .part2ElapsedSeconds(0)
                .part3ElapsedSeconds(0)
                .part4ElapsedSeconds(0)
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
