package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.CreateCardRequest;
import mobile.businesses.boundaries.vocab.CreateCardBoundary;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
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

