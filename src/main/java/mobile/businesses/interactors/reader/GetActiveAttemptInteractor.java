package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.reader.GetActiveAttemptBoundary;
import mobile.databases.entities.reader.ToeicTestAttemptEntity;
import mobile.databases.repositories.reader.ToeicTestAttemptRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetActiveAttemptInteractor implements GetActiveAttemptBoundary {

    private final ToeicTestAttemptRepository attemptRepository;
    private final ToeicReaderMapper mapper;

    @Override
    public Response execute(Request request) {
        Optional<ToeicTestAttemptEntity> opt;
        if (request.getTestId() != null && !request.getTestId().trim().isEmpty()) {
            opt = attemptRepository.findFirstByUserIdAndTestIdAndStatusOrderByStartedAtDesc(
                    request.getUserId(), request.getTestId(), "in_progress");
        } else {
            opt = attemptRepository.findFirstByUserIdAndStatusOrderByLastSavedAtDesc(
                    request.getUserId(), "in_progress");
        }

        return Response.builder()
                .data(opt.map(mapper::toAttemptDto).orElse(null))
                .build();
    }
}
