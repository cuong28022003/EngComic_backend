package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.CardDetailResponseDto;
import mobile.apis.vocab.dtos.CardResponseDto;
import mobile.businesses.boundaries.vocab.GetCardDetail;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.entities.vocab.WordRelation;
import mobile.databases.repositories.vocab.CardRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

        // Dynamically auto-link any relations that don't have relatedCardId yet
        if (userId != null && card.getRelations() != null && !card.getRelations().isEmpty()) {
            boolean modified = false;
            for (WordRelation r : card.getRelations()) {
                if (r.getRelatedCardId() == null && r.getText() != null && !r.getText().trim().isEmpty()) {
                    Optional<CardEntity> matching = cardRepository.findByUserIdAndWordIgnoreCase(userId, r.getText().trim());
                    if (matching.isPresent() && !matching.get().getId().equals(card.getId())) {
                        r.setRelatedCardId(matching.get().getId());
                        modified = true;
                    }
                }
            }
            if (modified) {
                card = cardRepository.save(card);
            }
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
