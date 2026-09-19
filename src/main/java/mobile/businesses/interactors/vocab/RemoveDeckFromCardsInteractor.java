package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.vocab.RemoveDeckFromCards;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoveDeckFromCardsInteractor implements RemoveDeckFromCards {

    private final CardRepository cardRepository;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        List<String> cardIds = request.getCardIds();
        String deckId = request.getDeckId();

        if (cardIds == null || cardIds.isEmpty()) {
            return Response.builder()
                    .totalRemoved(0)
                    .message("Không có thẻ từ nào được chọn")
                    .build();
        }
        if (deckId == null || deckId.isBlank()) {
            return Response.builder()
                    .totalRemoved(0)
                    .message("Vui lòng cung cấp bộ thẻ cần gỡ")
                    .build();
        }

        List<CardEntity> cards = cardRepository.findAllById(cardIds);
        if (userId != null) {
            cards = cards.stream().filter(c -> userId.equals(c.getUserId())).toList();
        }
        if (cards.isEmpty()) {
            return Response.builder()
                    .totalRemoved(0)
                    .message("Không tìm thấy thẻ từ nào")
                    .build();
        }

        int processed = 0;
        for (CardEntity card : cards) {
            boolean primaryRemoved = deckId.equals(card.getDeckId());
            List<String> remaining = (card.getDeckIds() != null ? card.getDeckIds() : new ArrayList<String>())
                    .stream()
                    .filter(d -> !deckId.equals(d))
                    .distinct()
                    .toList();
            boolean changed = primaryRemoved || (card.getDeckIds() != null && card.getDeckIds().contains(deckId));
            if (!changed) {
                continue;
            }
            card.setDeckIds(new ArrayList<>(remaining));
            if (primaryRemoved) {
                card.setDeckId(remaining.isEmpty() ? null : remaining.get(0));
            }
            cardRepository.save(card);
            processed++;
        }

        String msg = "Đã gỡ " + processed + " thẻ từ khỏi bộ thẻ";
        return Response.builder()
                .totalRemoved(processed)
                .message(msg)
                .build();
    }
}