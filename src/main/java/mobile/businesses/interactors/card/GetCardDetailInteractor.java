package mobile.businesses.interactors.card;

import lombok.RequiredArgsConstructor;
import mobile.apis.card.dtos.CardDetailResponseDto;
import mobile.apis.card.dtos.CardResponseDto;
import mobile.businesses.boundaries.card.GetCardDetail;
import mobile.databases.entities.card.CardEntity;
import mobile.databases.repositories.card.CardRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetCardDetailInteractor implements GetCardDetail {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public Response execute(Request request) {
        String cardId = request.getCardId();
        String userId = request.getUserId();

        CardEntity card = cardRepository.findById(cardId).orElse(null);
        if (card == null) {
            return Response.builder().data(null).build();
        }

        CardResponseDto cardResponse = cardMapper.toResponse(card);

        List<CardEntity> reverseCardEntities = (userId != null)
                ? cardRepository.findReverseRelations(userId, cardId)
                : Collections.emptyList();

        List<CardResponseDto> reverseResponses = reverseCardEntities.stream()
                .map(cardMapper::toResponse)
                .collect(Collectors.toList());

        CardDetailResponseDto dto = new CardDetailResponseDto(cardResponse, reverseResponses);
        return Response.builder().data(dto).build();
    }
}
