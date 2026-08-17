package mobile.businesses.interactors.card;

import lombok.RequiredArgsConstructor;
import mobile.apis.card.dtos.CardResponseDto;
import mobile.businesses.boundaries.card.UpdateCard;
import mobile.databases.entities.card.CardEntity;
import mobile.databases.repositories.card.CardRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateCardInteractor implements UpdateCard {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public Response execute(Request request) {
        CardEntity existingCard = cardRepository.findById(request.getCardId()).orElse(null);
        if (existingCard != null) {
            CardEntity updatedCard = cardMapper.toEntity(request.getPayload());
            updatedCard.setId(request.getCardId());
            updatedCard.setCreateAt(existingCard.getCreateAt());
            CardEntity savedCard = cardRepository.save(updatedCard);
            CardResponseDto dto = cardMapper.toResponse(savedCard);
            return Response.builder().card(dto).build();
        }
        return Response.builder().card(null).build();
    }
}
