package mobile.businesses.interactors.card;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.card.CreateCardBoundary;
import mobile.mapping.CardMapping;
import mobile.model.Entity.Card;
import mobile.model.payload.response.card.CardResponse;
import mobile.Service.CardService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCardInteractor implements CreateCardBoundary {
    private final CardService cardService;

    @Override
    public CardResponse execute(Request request) {
        Card cardEntity = CardMapping.createRequestToEntity(request.payload());
        if (cardEntity.getUserId() == null && request.currentUserId() != null) {
            cardEntity.setUserId(new ObjectId(request.currentUserId()));
        }

        Card savedCard = cardService.save(cardEntity);
        return CardMapping.entityToResponse(savedCard);
    }
}
