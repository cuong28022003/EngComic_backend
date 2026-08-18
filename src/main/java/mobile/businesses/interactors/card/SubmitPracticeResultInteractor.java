package mobile.businesses.interactors.card;

import lombok.RequiredArgsConstructor;
import mobile.apis.card.dtos.CardResponseDto;
import mobile.businesses.boundaries.card.SubmitPracticeResult;
import mobile.databases.entities.card.CardEntity;
import mobile.databases.repositories.card.CardRepository;
import mobile.domains.card.CardRules;
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
            card.setStage(CardRules.calculateNextStage(card.getStage(), card.getRepetition(), quality));
        } else {
            card.setLapses(card.getLapses() + 1);
            card.setWrongCount(card.getWrongCount() + 1);
        }

        CardRules.SrsStateInput currentState = new CardRules.SrsStateInput(
                card.getRepetition(),
                card.getEaseFactor(),
                card.getInterval()
        );

        CardRules.SrsCalculationResult result = CardRules.calculateSM2(currentState, quality);

        card.setRepetition(result.nextRepetition());
        card.setInterval(result.nextIntervalDays());
        card.setEaseFactor(result.nextEaseFactor());
        card.setNextReview(result.nextReviewAt());
        card.setLastReviewed(new Date());
        card.setReviewCount(card.getReviewCount() + 1);

        card.setStatus(CardRules.determineStatus(card.getWrongCount(), card.getInterval(), card.getRepetition()));

        CardEntity saved = cardRepository.save(card);
        CardResponseDto dto = cardMapper.toResponse(saved);
        return Response.builder().card(dto).build();
    }
}
