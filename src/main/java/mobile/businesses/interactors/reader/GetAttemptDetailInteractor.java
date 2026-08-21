package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.reader.GetAttemptDetailBoundary;
import mobile.databases.entities.reader.ToeicTestAttemptEntity;
import mobile.databases.repositories.reader.ToeicTestAttemptRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GetAttemptDetailInteractor implements GetAttemptDetailBoundary {

    private final ToeicTestAttemptRepository attemptRepository;
    private final ToeicReaderMapper mapper;

    @Override
    public Response execute(Request request) {
        ToeicTestAttemptEntity attempt = attemptRepository.findByIdAndUserId(request.getAttemptId(), request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lượt làm bài"));

        return Response.builder()
                .data(mapper.toAttemptDto(attempt))
                .build();
    }
}
