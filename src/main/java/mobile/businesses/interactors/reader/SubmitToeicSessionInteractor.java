package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.*;
import mobile.businesses.boundaries.reader.SubmitToeicSessionBoundary;
import mobile.businesses.domains.reader.ScaledScoreConverter;
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
        boolean isListening = "listening".equals(test.getSection());
        List<Integer> defaultParts = isListening ? Arrays.asList(1, 2, 3, 4) : Arrays.asList(5, 6, 7);
        List<Integer> selectedParts = (sub != null && sub.getSelectedParts() != null && !sub.getSelectedParts().isEmpty())
                ? sub.getSelectedParts() : defaultParts;

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

        Integer scaledScore = ScaledScoreConverter.convert(rawScore, totalQuestions, test.getSection());

        // Save session record (backward compatibility)
        ToeicUserSessionEntity session = ToeicUserSessionEntity.builder()
                .userId(request.getUserId())
                .testId(test.getId())
                .testName(test.getTestName())
                .rawScore(rawScore)
                .scaledScore(scaledScore)
                .totalQuestions(totalQuestions)
                .duration(sub != null ? sub.getDuration() : 0)
                .timeMode(sub != null ? sub.getTimeMode() : "full_test")
                .selectedParts(selectedParts)
                .part1TargetSeconds(getPartTargetSeconds(sub, 1))
                .part2TargetSeconds(getPartTargetSeconds(sub, 2))
                .part3TargetSeconds(getPartTargetSeconds(sub, 3))
                .part4TargetSeconds(getPartTargetSeconds(sub, 4))
                .part5TargetSeconds(getPartTargetSeconds(sub, 5))
                .part6TargetSeconds(getPartTargetSeconds(sub, 6))
                .part7TargetSeconds(getPartTargetSeconds(sub, 7))
                .part1ElapsedSeconds(getPartElapsedSeconds(sub, 1))
                .part2ElapsedSeconds(getPartElapsedSeconds(sub, 2))
                .part3ElapsedSeconds(getPartElapsedSeconds(sub, 3))
                .part4ElapsedSeconds(getPartElapsedSeconds(sub, 4))
                .part5ElapsedSeconds(getPartElapsedSeconds(sub, 5))
                .part6ElapsedSeconds(getPartElapsedSeconds(sub, 6))
                .part7ElapsedSeconds(getPartElapsedSeconds(sub, 7))
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
        attempt.setPart1TargetSeconds(getPartTargetSeconds(sub, 1));
        attempt.setPart2TargetSeconds(getPartTargetSeconds(sub, 2));
        attempt.setPart3TargetSeconds(getPartTargetSeconds(sub, 3));
        attempt.setPart4TargetSeconds(getPartTargetSeconds(sub, 4));
        attempt.setPart5TargetSeconds(getPartTargetSeconds(sub, 5));
        attempt.setPart6TargetSeconds(getPartTargetSeconds(sub, 6));
        attempt.setPart7TargetSeconds(getPartTargetSeconds(sub, 7));
        attempt.setTotalElapsedSeconds(sub != null ? sub.getDuration() : 0);
        attempt.setPart1ElapsedSeconds(getPartElapsedSeconds(sub, 1));
        attempt.setPart2ElapsedSeconds(getPartElapsedSeconds(sub, 2));
        attempt.setPart3ElapsedSeconds(getPartElapsedSeconds(sub, 3));
        attempt.setPart4ElapsedSeconds(getPartElapsedSeconds(sub, 4));
        attempt.setPart5ElapsedSeconds(getPartElapsedSeconds(sub, 5));
        attempt.setPart6ElapsedSeconds(getPartElapsedSeconds(sub, 6));
        attempt.setPart7ElapsedSeconds(getPartElapsedSeconds(sub, 7));
        attempt.setRawScore(rawScore);
        attempt.setScaledScore(scaledScore);
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
        if (scaledScore != null) {
            test.setScaledScore(scaledScore);
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
                        targetSec = getPartTargetSeconds(sub, p);
                        elapsedSec = getPartElapsedSeconds(sub, p);
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
                .scaledScore(scaledScore)
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

    private int getPartTargetSeconds(SubmitToeicSessionRequest sub, int part) {
        if (sub == null) return 0;
        switch (part) {
            case 1: return sub.getPart1TargetSeconds();
            case 2: return sub.getPart2TargetSeconds();
            case 3: return sub.getPart3TargetSeconds();
            case 4: return sub.getPart4TargetSeconds();
            case 5: return sub.getPart5TargetSeconds();
            case 6: return sub.getPart6TargetSeconds();
            case 7: return sub.getPart7TargetSeconds();
            default: return 0;
        }
    }

    private int getPartElapsedSeconds(SubmitToeicSessionRequest sub, int part) {
        if (sub == null) return 0;
        switch (part) {
            case 1: return sub.getPart1ElapsedSeconds();
            case 2: return sub.getPart2ElapsedSeconds();
            case 3: return sub.getPart3ElapsedSeconds();
            case 4: return sub.getPart4ElapsedSeconds();
            case 5: return sub.getPart5ElapsedSeconds();
            case 6: return sub.getPart6ElapsedSeconds();
            case 7: return sub.getPart7ElapsedSeconds();
            default: return 0;
        }
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
