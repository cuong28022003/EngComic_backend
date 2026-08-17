package mobile.businesses.interactors.card;

import lombok.RequiredArgsConstructor;
import mobile.apis.card.dtos.CardResponseDto;
import mobile.businesses.boundaries.card.GetUserCardsBoundary;
import mobile.databases.entities.card.CardEntity;
import mobile.databases.entities.deck.DeckEntity;
import mobile.databases.repositories.card.CardRepository;
import mobile.databases.repositories.deck.DeckRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserCardsInteractor implements GetUserCardsBoundary {
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final CardMapper cardMapper;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        String search = request.getSearch();
        boolean hasSearch = (search != null && !search.trim().isEmpty());

        Page<CardEntity> cardPage = hasSearch
                ? cardRepository.findByUserIdAndBackContainingIgnoreCaseOrFrontContainingIgnoreCase(userId, search.trim(), search.trim(), request.getPageable())
                : cardRepository.findByUserId(userId, request.getPageable());

        if (cardPage.getContent().isEmpty()) {
            Page<DeckEntity> userDecks = deckRepository.findByUserId(userId, PageRequest.of(0, 500));
            List<String> deckIds = userDecks.getContent().stream().map(DeckEntity::getId).collect(Collectors.toList());
            if (!deckIds.isEmpty()) {
                if (hasSearch) {
                    cardPage = cardRepository.findByDeckIdInOrDeckIdNullAndSearch(deckIds, search.trim(), request.getPageable());
                } else {
                    cardPage = cardRepository.findByDeckIdInOrDeckIdNull(deckIds, request.getPageable());
                }
            }
        }

        return Response.builder()
                .cards(cardPage.map(cardMapper::toResponse))
                .build();
    }
}
