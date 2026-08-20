package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.CardResponseDto;
import mobile.businesses.boundaries.vocab.GetUserCardsBoundary;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.entities.vocab.DeckEntity;
import mobile.databases.repositories.vocab.CardRepository;
import mobile.databases.repositories.vocab.DeckRepository;
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
                ? cardRepository.findByUserIdAndMeaningContainingIgnoreCaseOrWordContainingIgnoreCase(userId, search.trim(), search.trim(), request.getPageable())
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

