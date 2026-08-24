package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.CardResponseDto;
import mobile.businesses.boundaries.vocab.GetLeechCardsBoundary;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetLeechCardsInteractor implements GetLeechCardsBoundary {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        List<CardEntity> leechCards = cardRepository.findByUserIdAndStatus(userId, "leech");

        List<CardResponseDto> dtos = new ArrayList<>();
        for (CardEntity card : leechCards) {
            dtos.add(cardMapper.toResponse(card));
        }

        return Response.builder().cards(dtos).build();
    }
}
