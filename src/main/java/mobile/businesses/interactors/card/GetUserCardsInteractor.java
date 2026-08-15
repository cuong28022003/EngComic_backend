package mobile.businesses.interactors.card;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.card.GetUserCardsBoundary;
import mobile.mapping.CardMapping;
import mobile.model.Entity.Card;
import mobile.model.Entity.Deck;
import mobile.model.payload.response.card.CardResponse;
import mobile.repository.CardRepository;
import mobile.Service.DeckService;
import mobile.Service.CardService;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserCardsInteractor implements GetUserCardsBoundary {
    private final CardRepository cardRepository;
    private final CardService cardService;
    private final DeckService deckService;

    @Override
    public Page<CardResponse> execute(Request request) {
        ObjectId userObjId = new ObjectId(request.userId());
        String search = request.search();
        boolean hasSearch = (search != null && !search.trim().isEmpty());

        Page<Card> cardPage = hasSearch
                ? cardRepository.findByUserIdAndBackContainingIgnoreCaseOrFrontContainingIgnoreCase(userObjId, search.trim(), search.trim(), request.pageable())
                : cardRepository.findByUserId(userObjId, request.pageable());

        if (cardPage.getContent().isEmpty()) {
            Page<Deck> userDecks = deckService.findByUserId(userObjId, PageRequest.of(0, 500));
            List<ObjectId> deckIds = userDecks.getContent().stream().map(Deck::getId).collect(Collectors.toList());
            cardPage = cardService.findByDeckIdIn(deckIds, search, request.pageable());
        }

        return cardPage.map(CardMapping::entityToResponse);
    }
}
