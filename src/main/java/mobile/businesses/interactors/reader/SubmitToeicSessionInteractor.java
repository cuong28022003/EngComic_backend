package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.*;
import mobile.businesses.boundaries.reader.SubmitToeicSessionBoundary;
import mobile.databases.entities.reader.ToeicQuestion;
import mobile.databases.entities.reader.ToeicReviewItemEntity;
import mobile.databases.entities.reader.ToeicTestAttemptEntity;
import mobile.databases.entities.reader.ToeicTestEntity;
import mobile.databases.entities.reader.ToeicUserSessionEntity;
import mobile.databases.repositories.reader.ToeicReviewItemRepository;
import mobile.databases.repositories.reader.ToeicTestAttemptRepository;
import mobile.databases.repositories.reader.ToeicTestRepository;
import mobile.databases.repositories.reader.ToeicUserSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmitToeicSessionInteractor implements SubmitToeicSessionBoundary {

    private final ToeicTestRepository testRepository;
    private final ToeicUserSessionRepository sessionRepository;
    private final ToeicTestAttemptRepository attemptRepository;
    private final ToeicReviewItemRepository reviewItemRepository;
    private final mobile.businesses.boundaries.user.RecordStudyActivity recordStudyActivity;

    @Override
    @Transactional
    public Response execute(Request request) {
        ToeicTestEntity test = testRepository.findByIdAndUserId(request.getTestId(), request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài thi"));

        SubmitToeicSessionRequest sub = request.getSubmissionData();
        Map<Integer, SubmitToeicSessionRequest.UserAnswerItem> answerMap = new HashMap<>();
        if (sub != null && sub.getAnswers() != null) {
            for (SubmitToeicSessionRequest.UserAnswerItem item : sub.getAnswers()) {
                answerMap.put(item.getQuestionNumber(), item);
            }
        }

        List<GradedQuestionDto> results = new ArrayList<>();
        List<ToeicUserSessionEntity.UserAnswerRecord> sessionAnswers = new ArrayList<>();
        List<ToeicReviewItemEntity> copiedReviewsToSave = new ArrayList<>();

        List<ToeicReviewItemEntity> existingReviews = reviewItemRepository.findByUserIdAndTestIdOrderByQuestionNumberAsc(request.getUserId(), test.getId());
        Map<Integer, ToeicReviewItemEntity> existingReviewMap = existingReviews.stream()
                .collect(Collectors.toMap(ToeicReviewItemEntity::getQuestionNumber, r -> r, (a, b) -> a));

        int rawScore = 0;
        List<ToeicQuestion> allQuestions = test.getQuestions() != null ? test.getQuestions() : Collections.emptyList();
        List<Integer> selectedParts = (sub != null && sub.getSelectedParts() != null && !sub.getSelectedParts().isEmpty())
                ? sub.getSelectedParts() : Arrays.asList(5, 6, 7);

        List<ToeicQuestion> questions = allQuestions.stream()
                .filter(q -> selectedParts.contains(q.getPart()))
                .collect(Collectors.toList());
        int totalQuestions = questions.size();

        Map<Integer, int[]> partStats = new TreeMap<>(); // part -> [correct, total]

        for (ToeicQuestion q : questions) {
            int part = q.getPart();
            partStats.putIfAbsent(part, new int[]{0, 0});
            partStats.get(part)[1]++;

            SubmitToeicSessionRequest.UserAnswerItem uAns = answerMap.get(q.getNumber());
            String userChoice = (uAns != null && uAns.getAnswer() != null) ? uAns.getAnswer().trim().toUpperCase() : null;
            boolean flagged = uAns != null && uAns.isFlagged();

            String correctChoice = q.getCorrectAnswer() != null ? q.getCorrectAnswer().trim().toUpperCase() : "";
            boolean isCorrect = userChoice != null && !userChoice.isEmpty() && userChoice.equalsIgnoreCase(correctChoice);

            if (isCorrect) {
                rawScore++;
                partStats.get(part)[0]++;

                if (flagged) {
                    ToeicReviewItemEntity existR = existingReviewMap.get(q.getNumber());
                    if (existR != null && existR.getExplanation() != null && !existR.getExplanation().trim().isEmpty()) {
                        copiedReviewsToSave.add(cloneReviewEntity(existR, request.getUserId(), test.getId(), q.getNumber()));
                    }
                }
            } else {
                ToeicReviewItemEntity existR = existingReviewMap.get(q.getNumber());
                if (existR != null && existR.getExplanation() != null && !existR.getExplanation().trim().isEmpty()) {
                    copiedReviewsToSave.add(cloneReviewEntity(existR, request.getUserId(), test.getId(), q.getNumber()));
                }
            }

            int timeSpent = (uAns != null) ? uAns.getTimeSpentSeconds() : 0;

            results.add(GradedQuestionDto.builder()
                    .questionNumber(q.getNumber())
                    .part(q.getPart())
                    .userAnswer(userChoice)
                    .correctAnswer(correctChoice)
                    .isCorrect(isCorrect)
                    .flagged(flagged)
                    .timeSpentSeconds(timeSpent)
                    .build());

            sessionAnswers.add(ToeicUserSessionEntity.UserAnswerRecord.builder()
                    .questionNumber(q.getNumber())
                    .part(q.getPart())
                    .userAnswer(userChoice)
                    .correctAnswer(correctChoice)
                    .isCorrect(isCorrect)
                    .flagged(flagged)
                    .timeSpentSeconds(timeSpent)
                    .build());
        }

        ToeicTestAttemptEntity attempt = null;
        if (sub != null && sub.getAttemptId() != null && !sub.getAttemptId().trim().isEmpty()) {
            attempt = attemptRepository.findByIdAndUserId(sub.getAttemptId(), request.getUserId()).orElse(null);
        }
        if (attempt == null) {
            // Find active attempt if any, or create new one
            attempt = attemptRepository.findFirstByUserIdAndTestIdAndStatusOrderByStartedAtDesc(
                    request.getUserId(), test.getId(), "in_progress").orElse(null);
        }

        int attemptNumber = 1;
        if (attempt == null) {
            long prevCount = attemptRepository.countByUserIdAndTestId(request.getUserId(), test.getId());
            attemptNumber = (int) prevCount + 1;
            attempt = ToeicTestAttemptEntity.builder()
                    .userId(request.getUserId())
                    .testId(test.getId())
                    .testName(test.getTestName())
                    .attemptNumber(attemptNumber)
                    .startedAt(new Date())
                    .build();
        } else {
            attemptNumber = attempt.getAttemptNumber();
        }

        // Save session record (backward compatibility)
        ToeicUserSessionEntity session = ToeicUserSessionEntity.builder()
                .userId(request.getUserId())
                .testId(test.getId())
                .testName(test.getTestName())
                .rawScore(rawScore)
                .totalQuestions(totalQuestions)
                .duration(sub != null ? sub.getDuration() : 0)
                .timeMode(sub != null ? sub.getTimeMode() : "full_test")
                .selectedParts(selectedParts)
                .part5TargetSeconds(sub != null ? sub.getPart5TargetSeconds() : 0)
                .part6TargetSeconds(sub != null ? sub.getPart6TargetSeconds() : 0)
                .part7TargetSeconds(sub != null ? sub.getPart7TargetSeconds() : 0)
                .part5ElapsedSeconds(sub != null ? sub.getPart5ElapsedSeconds() : 0)
                .part6ElapsedSeconds(sub != null ? sub.getPart6ElapsedSeconds() : 0)
                .part7ElapsedSeconds(sub != null ? sub.getPart7ElapsedSeconds() : 0)
                .answers(sessionAnswers)
                .submittedAt(new Date())
                .build();
        sessionRepository.save(session);

        List<ToeicTestAttemptEntity.UserAnswerRecord> attemptAnswers = sessionAnswers.stream()
                .map(sa -> ToeicTestAttemptEntity.UserAnswerRecord.builder()
                        .questionNumber(sa.getQuestionNumber())
                        .part(sa.getPart())
                        .userAnswer(sa.getUserAnswer())
                        .correctAnswer(sa.getCorrectAnswer())
                        .isCorrect(sa.isCorrect())
                        .flagged(sa.isFlagged())
                        .timeSpentSeconds(sa.getTimeSpentSeconds())
                        .build())
                .collect(Collectors.toList());

        attempt.setStatus("completed");
        attempt.setTimeMode(sub != null ? sub.getTimeMode() : "full_test");
        attempt.setSelectedParts(selectedParts);
        attempt.setPart5TargetSeconds(sub != null ? sub.getPart5TargetSeconds() : 0);
        attempt.setPart6TargetSeconds(sub != null ? sub.getPart6TargetSeconds() : 0);
        attempt.setPart7TargetSeconds(sub != null ? sub.getPart7TargetSeconds() : 0);
        attempt.setTotalElapsedSeconds(sub != null ? sub.getDuration() : 0);
        attempt.setPart5ElapsedSeconds(sub != null ? sub.getPart5ElapsedSeconds() : 0);
        attempt.setPart6ElapsedSeconds(sub != null ? sub.getPart6ElapsedSeconds() : 0);
        attempt.setPart7ElapsedSeconds(sub != null ? sub.getPart7ElapsedSeconds() : 0);
        attempt.setRawScore(rawScore);
        attempt.setTotalQuestions(totalQuestions);
        attempt.setAnswers(attemptAnswers);
        attempt.setLastSavedAt(new Date());
        attempt.setCompletedAt(new Date());
        attempt = attemptRepository.save(attempt);

        final String finalAttemptId = attempt.getId();
        if (!copiedReviewsToSave.isEmpty()) {
            copiedReviewsToSave.forEach(r -> r.setAttemptId(finalAttemptId));
            reviewItemRepository.saveAll(copiedReviewsToSave);
        }

        // Update test entity (keep best/latest score)
        test.setStatus("completed");
        if (test.getRawScore() == null || rawScore >= test.getRawScore()) {
            test.setRawScore(rawScore);
        }
        test.setUpdatedAt(new Date());
        testRepository.save(test);

        // Part breakdown
        List<PartBreakdownDto> partBreakdown = partStats.entrySet().stream()
                .map(e -> {
                    int p = e.getKey();
                    int correct = e.getValue()[0];
                    int total = e.getValue()[1];
                    double acc = total > 0 ? ((double) correct / total) * 100.0 : 0.0;

                    int targetSec = 0;
                    int elapsedSec = 0;
                    if (sub != null) {
                        if (p == 5) {
                            targetSec = sub.getPart5TargetSeconds();
                            elapsedSec = sub.getPart5ElapsedSeconds();
                        } else if (p == 6) {
                            targetSec = sub.getPart6TargetSeconds();
                            elapsedSec = sub.getPart6ElapsedSeconds();
                        } else if (p == 7) {
                            targetSec = sub.getPart7TargetSeconds();
                            elapsedSec = sub.getPart7ElapsedSeconds();
                        }
                    }
                    double avgSec = total > 0 ? (double) elapsedSec / total : 0.0;

                    return PartBreakdownDto.builder()
                            .part(p)
                            .correctCount(correct)
                            .totalCount(total)
                            .accuracyPercentage(Math.round(acc * 10.0) / 10.0)
                            .targetSeconds(targetSec)
                            .elapsedSeconds(elapsedSec)
                            .avgSecondsPerQuestion(Math.round(avgSec * 10.0) / 10.0)
                            .build();
                })
                .collect(Collectors.toList());

        double overallAcc = totalQuestions > 0 ? ((double) rawScore / totalQuestions) * 100.0 : 0.0;

        SubmitToeicSessionResponse res = SubmitToeicSessionResponse.builder()
                .testId(test.getId())
                .testName(test.getTestName())
                .attemptId(attempt.getId())
                .attemptNumber(attemptNumber)
                .rawScore(rawScore)
                .totalQuestions(totalQuestions)
                .accuracyPercentage(Math.round(overallAcc * 10.0) / 10.0)
                .duration(sub != null ? sub.getDuration() : 0)
                .partBreakdown(partBreakdown)
                .results(results)
                .build();

        // Record user study activity, update streak & award XP
        try {
            int earnedXp = Math.max(30, rawScore * 2);
            recordStudyActivity.execute(mobile.businesses.boundaries.user.RecordStudyActivity.Request.builder()
                    .userId(request.getUserId())
                    .xpEarned(earnedXp)
                    .activityType("toeic_test")
                    .build());
        } catch (Exception e) {
            // Non-blocking for test completion
        }

        return Response.builder()
                .data(res)
                .build();
    }

    private ToeicReviewItemEntity cloneReviewEntity(
            ToeicReviewItemEntity source,
            String userId,
            String testId,
            int questionNumber) {
        return ToeicReviewItemEntity.builder()
                .userId(userId)
                .testId(testId)
                .questionNumber(questionNumber)
                .part(source.getPart())
                .errorType(source.getErrorType())
                .errorSubtype(source.getErrorSubtype())
                .passageExcerpt(source.getPassageExcerpt())
                .questionText(source.getQuestionText())
                .options(source.getOptions() != null ? new HashMap<>(source.getOptions()) : new HashMap<>())
                .explanation(source.getExplanation())
                .tip(source.getTip())
                .keyVocab(source.getKeyVocab() != null ? new ArrayList<>(source.getKeyVocab()) : new ArrayList<>())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
    }
}
