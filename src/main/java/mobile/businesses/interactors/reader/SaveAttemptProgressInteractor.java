package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.SaveProgressRequest;
import mobile.businesses.boundaries.reader.SaveAttemptProgressBoundary;
import mobile.databases.entities.reader.ToeicTestAttemptEntity;
import mobile.databases.repositories.reader.ToeicTestAttemptRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SaveAttemptProgressInteractor implements SaveAttemptProgressBoundary {

    private final ToeicTestAttemptRepository attemptRepository;
    private final ToeicReaderMapper mapper;

    @Override
    @Transactional
    public Response execute(Request request) {
        ToeicTestAttemptEntity attempt;
        if (request.getUserId() != null && !request.getUserId().trim().isEmpty()) {
            attempt = attemptRepository.findByIdAndUserId(request.getAttemptId(), request.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lượt làm bài"));
        } else {
            attempt = attemptRepository.findById(request.getAttemptId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lượt làm bài"));
        }

        if (!"in_progress".equalsIgnoreCase(attempt.getStatus())) {
            // Đã hoàn thành hoặc bỏ dở, không auto-save đè lên
            return Response.builder().data(mapper.toAttemptDto(attempt)).build();
        }

        SaveProgressRequest prog = request.getProgressData();
        if (prog != null) {
            attempt.setTotalElapsedSeconds(prog.getTotalElapsedSeconds());
            attempt.setPart5ElapsedSeconds(prog.getPart5ElapsedSeconds());
            attempt.setPart6ElapsedSeconds(prog.getPart6ElapsedSeconds());
            attempt.setPart7ElapsedSeconds(prog.getPart7ElapsedSeconds());

            if (prog.getAnswers() != null) {
                // Merge answers
                Map<Integer, ToeicTestAttemptEntity.UserAnswerRecord> map = new HashMap<>();
                if (attempt.getAnswers() != null) {
                    for (ToeicTestAttemptEntity.UserAnswerRecord a : attempt.getAnswers()) {
                        map.put(a.getQuestionNumber(), a);
                    }
                }

                for (SaveProgressRequest.AnswerItem item : prog.getAnswers()) {
                    ToeicTestAttemptEntity.UserAnswerRecord existing = map.get(item.getQuestionNumber());
                    if (existing != null) {
                        existing.setUserAnswer(item.getAnswer());
                        existing.setFlagged(item.isFlagged());
                        existing.setTimeSpentSeconds(item.getTimeSpentSeconds());
                    } else {
                        map.put(item.getQuestionNumber(), ToeicTestAttemptEntity.UserAnswerRecord.builder()
                                .questionNumber(item.getQuestionNumber())
                                .part(item.getPart())
                                .userAnswer(item.getAnswer())
                                .flagged(item.isFlagged())
                                .timeSpentSeconds(item.getTimeSpentSeconds())
                                .build());
                    }
                }

                List<ToeicTestAttemptEntity.UserAnswerRecord> updatedList = new ArrayList<>(map.values());
                updatedList.sort(Comparator.comparingInt(ToeicTestAttemptEntity.UserAnswerRecord::getQuestionNumber));
                attempt.setAnswers(updatedList);
            }

            attempt.setLastSavedAt(new Date());
            attempt = attemptRepository.save(attempt);
        }

        return Response.builder()
                .data(mapper.toAttemptDto(attempt))
                .build();
    }
}
