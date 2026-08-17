package mobile.businesses.interactors.card;

import lombok.RequiredArgsConstructor;
import mobile.apis.card.dtos.CardResponseDto;
import mobile.businesses.boundaries.card.SubmitPracticeResult;
import mobile.databases.entities.card.CardEntity;
import mobile.databases.repositories.card.CardRepository;
import mobile.domains.card.SrsAlgorithmRules;
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
            card.setStage(SrsAlgorithmRules.calculateNextStage(card.getStage(), card.getRepetition(), quality));
        } else {
            card.setLapses(card.getLapses() + 1);
            card.setWrongCount(card.getWrongCount() + 1);
        }

        SrsAlgorithmRules.SrsCalculationResult result = SrsAlgorithmRules.calculateSM2(
                card.getRepetition(),
                card.getInterval(),
                card.getEaseFactor(),
                quality
        );

        card.setRepetition(result.repetition());
        card.setInterval(result.interval());
        card.setEaseFactor(result.easeFactor());
        card.setNextReview(result.nextReviewDate());
        card.setLastReviewed(new Date());
        card.setReviewCount(card.getReviewCount() + 1);

        card.setStatus(SrsAlgorithmRules.determineStatus(card.getWrongCount(), card.getInterval(), card.getRepetition()));

        CardEntity saved = cardRepository.save(card);
        CardResponseDto dto = cardMapper.toResponse(saved);
        return Response.builder().card(dto).build();
    }
}
