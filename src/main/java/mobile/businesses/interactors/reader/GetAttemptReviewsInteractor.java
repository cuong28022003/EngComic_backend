package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.reader.GetAttemptReviewsBoundary;
import mobile.databases.entities.reader.ToeicReviewItemEntity;
import mobile.databases.repositories.reader.ToeicReviewItemRepository;
import mobile.databases.repositories.reader.ToeicTestAttemptRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAttemptReviewsInteractor implements GetAttemptReviewsBoundary {

    private final ToeicTestAttemptRepository attemptRepository;
    private final ToeicReviewItemRepository reviewItemRepository;
    private final ToeicReaderMapper mapper;

    @Override
    public Response execute(Request request) {
        List<ToeicReviewItemEntity> list = reviewItemRepository
                .findByUserIdAndAttemptIdOrderByQuestionNumberAsc(request.getUserId(), request.getAttemptId());

        String testId = null;
        var attemptOpt = attemptRepository.findByIdAndUserId(request.getAttemptId(), request.getUserId());
        if (attemptOpt.isPresent()) {
            testId = attemptOpt.get().getTestId();
        }

        // If no attempt-specific reviews, try to load any reviews for this test
        if (list.isEmpty() && testId != null) {
            list = reviewItemRepository.findByUserIdAndTestIdOrderByQuestionNumberAsc(request.getUserId(), testId);
        }

        return Response.builder()
                .data(list.stream().map(mapper::toReviewItemDto).collect(Collectors.toList()))
                .build();
    }
}
