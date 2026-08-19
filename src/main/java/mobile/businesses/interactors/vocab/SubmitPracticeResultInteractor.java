package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.CardResponseDto;
import mobile.businesses.boundaries.vocab.SubmitPracticeResult;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
import mobile.domains.vocab.VocabRules;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class SubmitPracticeResultInteractor implements SubmitPracticeResult {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public Response execute(Request request) {
        String cardId = request.getCardId();
        int quality = request.getQuality();

        CardEntity card = cardRepository.findById(cardId).orElse(null);
        if (card == null) {
            return Response.builder().card(null).build();
        }

        if (quality >= 3) {
            card.setStage(VocabRules.calculateNextStage(card.getStage(), card.getRepetition(), quality));
        } else {
            card.setLapses(card.getLapses() + 1);
            card.setWrongCount(card.getWrongCount() + 1);
        }

        VocabRules.SrsStateInput currentState = new VocabRules.SrsStateInput(
                card.getRepetition(),
                card.getEaseFactor(),
                card.getInterval()
        );

        VocabRules.SrsCalculationResult result = VocabRules.calculateSM2(currentState, quality);

        card.setRepetition(result.nextRepetition());
        card.setInterval(result.nextIntervalDays());
        card.setEaseFactor(result.nextEaseFactor());
        card.setNextReview(result.nextReviewAt());
        card.setLastReviewed(new Date());
        card.setReviewCount(card.getReviewCount() + 1);

        card.setStatus(VocabRules.determineStatus(card.getWrongCount(), card.getInterval(), card.getRepetition()));

        CardEntity saved = cardRepository.save(card);
        CardResponseDto dto = cardMapper.toResponse(saved);
        return Response.builder().card(dto).build();
    }
}

