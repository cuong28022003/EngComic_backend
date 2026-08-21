package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.reader.GetTestAttemptsBoundary;
import mobile.databases.entities.reader.ToeicTestAttemptEntity;
import mobile.databases.repositories.reader.ToeicTestAttemptRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTestAttemptsInteractor implements GetTestAttemptsBoundary {

    private final ToeicTestAttemptRepository attemptRepository;
    private final ToeicReaderMapper mapper;

    @Override
    public Response execute(Request request) {
        List<ToeicTestAttemptEntity> attempts = attemptRepository.findByUserIdAndTestIdOrderByStartedAtDesc(
                request.getUserId(), request.getTestId());

        return Response.builder()
                .data(attempts.stream().map(mapper::toAttemptDto).collect(Collectors.toList()))
                .build();
    }
}
