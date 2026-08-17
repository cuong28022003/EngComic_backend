package mobile.businesses.interactors.card;

import lombok.RequiredArgsConstructor;
import mobile.apis.card.dtos.CreateCardRequest;
import mobile.businesses.boundaries.card.CreateCardBoundary;
import mobile.databases.entities.card.CardEntity;
import mobile.databases.repositories.card.CardRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCardInteractor implements CreateCardBoundary {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public Response execute(Request request) {
        CreateCardRequest payload = request.getPayload();
        CardEntity cardEntity = cardMapper.toEntity(payload);
        if (cardEntity.getUserId() == null && request.getCurrentUserId() != null) {
            cardEntity.setUserId(request.getCurrentUserId());
        }

        CardEntity savedCard = cardRepository.save(cardEntity);
        return Response.builder()
                .card(cardMapper.toResponse(savedCard))
                .build();
    }
}
