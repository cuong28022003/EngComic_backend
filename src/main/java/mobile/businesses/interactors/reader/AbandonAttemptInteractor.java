package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.reader.AbandonAttemptBoundary;
import mobile.databases.entities.reader.ToeicTestAttemptEntity;
import mobile.databases.repositories.reader.ToeicTestAttemptRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AbandonAttemptInteractor implements AbandonAttemptBoundary {

    private final ToeicTestAttemptRepository attemptRepository;
    private final ToeicReaderMapper mapper;

    @Override
    @Transactional
    public Response execute(Request request) {
        ToeicTestAttemptEntity attempt = attemptRepository.findByIdAndUserId(request.getAttemptId(), request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lượt làm bài"));

        if ("in_progress".equalsIgnoreCase(attempt.getStatus())) {
            attempt.setStatus("abandoned");
            attempt.setLastSavedAt(new Date());
            attempt = attemptRepository.save(attempt);
        }

        return Response.builder()
                .data(mapper.toAttemptDto(attempt))
                .build();
    }
}
