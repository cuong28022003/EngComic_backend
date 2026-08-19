package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.CardResponseDto;
import mobile.businesses.boundaries.vocab.UpdateCard;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
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

