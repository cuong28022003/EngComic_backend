package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.SubmitLevelAnswerRequest;
import mobile.apis.vocab.dtos.SubmitLevelAnswerResponseDto;
import mobile.businesses.boundaries.vocab.SubmitLevelAnswerBoundary;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubmitLevelAnswerInteractor implements SubmitLevelAnswerBoundary {

    private final CardRepository cardRepository;
    private final mobile.businesses.boundaries.user.RecordStudyActivity recordStudyActivity;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        String cardId = request.getCardId();
        SubmitLevelAnswerRequest payload = request.getPayload();

        Optional<CardEntity> cardOpt = cardRepository.findByIdAndUserId(cardId, userId);
        if (cardOpt.isEmpty()) {
            return Response.builder()
                    .data(SubmitLevelAnswerResponseDto.builder()
                            .cardId(cardId)
                            .message("Không tìm thấy thẻ từ vựng")
                            .build())
                    .build();
        }

        CardEntity card = cardOpt.get();
        int oldLevel = card.getMasteryLevel() > 0 ? card.getMasteryLevel() : 1;
        int currentLevel = payload.getCurrentLevel() > 0 ? payload.getCurrentLevel() : oldLevel;
        int quality = payload.getQuality();
        boolean isCorrect = payload.isCorrect() || quality >= 3;

        boolean levelPromoted = false;
        int newLevel = oldLevel;
        Date now = new Date();
        card.setLastReviewed(now);
        card.setReviewCount(card.getReviewCount() + 1);

        if (isCorrect) {
            card.setWrongCount(0);

            // Promote mastery level
            if (currentLevel >= oldLevel && oldLevel < 4) {
                newLevel = oldLevel + 1;
                card.setMasteryLevel(newLevel);
                levelPromoted = true;
            }

            // Record confidence score at Level 4
            if (currentLevel == 4 && payload.getConfidenceScore() != null) {
                card.setConfidenceScore(payload.getConfidenceScore());
            }

            // SM-2 Spaced Repetition Logic
            double ef = card.getEaseFactor() > 0 ? card.getEaseFactor() : 2.5;
            ef = Math.max(1.3, ef + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)));
            card.setEaseFactor(ef);

            int rep = card.getRepetition();
            int interval;
            if (rep == 0) {
                interval = 1;
            } else if (rep == 1) {
                interval = 6;
            } else {
                interval = (int) Math.round(card.getInterval() * ef);
            }

            // If confidence < 3 in Level 4, ratchet interval down by 30%
            if (currentLevel == 4 && payload.getConfidenceScore() != null && payload.getConfidenceScore() < 3) {
                interval = Math.max(1, (int) Math.round(interval * 0.7));
            }

            card.setInterval(Math.min(interval, 365));
            card.setRepetition(rep + 1);
            card.setStatus(interval >= 21 ? "mature" : "review");

            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_YEAR, card.getInterval());
            card.setNextReview(cal.getTime());

        } else {
            // Wrong answer handling
            card.setWrongCount(card.getWrongCount() + 1);
            card.setLapses(card.getLapses() + 1);
            card.setRepetition(0);
            card.setInterval(1);

            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            card.setNextReview(cal.getTime());

            // Leech detection (>= 4 consecutive failures)
            if (card.getWrongCount() >= 4) {
                card.setStatus("leech");
                card.setMasteryLevel(1);
                newLevel = 1;
            } else {
                card.setStatus("learning");
            }
        }

        card.setUpdateAt(now);
        cardRepository.save(card);

        // Record user study activity & streak
        if (userId != null && !userId.isBlank()) {
            try {
                recordStudyActivity.execute(mobile.businesses.boundaries.user.RecordStudyActivity.Request.builder()
                        .userId(userId)
                        .xpEarned(isCorrect ? 15 : 5)
                        .activityType("practice")
                        .build());
            } catch (Exception ignored) {}
        }

        boolean isLeech = "leech".equalsIgnoreCase(card.getStatus());
        String msg;
        if (isLeech) {
            msg = "Từ này đang bị kẹt (Leech)! Đã chuyển vào Leech Center để bổ sung mẹo nhớ.";
        } else if (levelPromoted) {
            msg = String.format("Xuất sắc! Từ vựng đã thăng hạng lên Level %d.", newLevel);
        } else if (isCorrect) {
            msg = "Trả lời chính xác!";
        } else {
            msg = "Đừng nản lòng, hãy ghi nhớ và ôn lại vào ngày mai nhé!";
        }

        SubmitLevelAnswerResponseDto responseDto = SubmitLevelAnswerResponseDto.builder()
                .cardId(card.getId())
                .word(card.getWord())
                .oldLevel(oldLevel)
                .newLevel(newLevel)
                .levelPromoted(levelPromoted)
                .status(card.getStatus())
                .intervalDays(card.getInterval())
                .nextReviewDate(card.getNextReview())
                .wrongCount(card.getWrongCount())
                .isLeech(isLeech)
                .message(msg)
                .build();

        return Response.builder().data(responseDto).build();
    }
}
