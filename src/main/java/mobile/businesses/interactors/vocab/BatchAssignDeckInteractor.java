package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.vocab.BatchAssignDeckBoundary;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BatchAssignDeckInteractor implements BatchAssignDeckBoundary {

    private final CardRepository cardRepository;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        List<String> cardIds = request.getCardIds();

        if (cardIds == null || cardIds.isEmpty()) {
            return Response.builder()
                    .totalAssigned(0)
                    .message("Không có thẻ từ nào được chọn")
                    .build();
        }

        List<CardEntity> cards = cardRepository.findAllById(cardIds);
        if (userId != null) {
            cards = cards.stream().filter(c -> userId.equals(c.getUserId())).toList();
        }
        if (cards.isEmpty()) {
            return Response.builder()
                    .totalAssigned(0)
                    .message("Không tìm thấy thẻ từ nào")
                    .build();
        }

        // deckIds != null => multi-deck mode: empty list clears membership, non-empty adds to those decks
        boolean multiMode = request.getDeckIds() != null;
        List<String> requestDecks = multiMode ? request.getDeckIds() : List.of();

        List<String> cleaned = requestDecks.stream()
                .filter(d -> d != null && !d.isBlank())
                .distinct()
                .toList();

        int processed = 0;
        for (CardEntity card : cards) {
            Set<String> merged = new LinkedHashSet<>();
            if (card.getDeckIds() != null) {
                merged.addAll(card.getDeckIds());
            }
            if (multiMode) {
                merged.addAll(cleaned);
                if (merged.isEmpty()) {
                    card.setDeckIds(new ArrayList<>());
                    card.setDeckId(null);
                } else {
                    card.setDeckIds(new ArrayList<>(merged));
                    if (card.getDeckId() == null || card.getDeckId().isBlank()) {
                        card.setDeckId(merged.iterator().next());
                    }
                }
            } else {
                // legacy single-deck replace mode
                String targetDeckId = (request.getDeckId() != null
                        && !request.getDeckId().isBlank()
                        && !"unassigned".equalsIgnoreCase(request.getDeckId().trim()))
                        ? request.getDeckId().trim()
                        : null;
                if (targetDeckId == null) {
                    card.setDeckIds(new ArrayList<>());
                    card.setDeckId(null);
                } else {
                    card.setDeckIds(new ArrayList<>(List.of(targetDeckId)));
                    card.setDeckId(targetDeckId);
                }
            }
            cardRepository.save(card);
            processed++;
        }

        String msg;
        if (multiMode && cleaned.isEmpty()) {
            msg = "Đã hủy gán bộ thẻ cho " + processed + " thẻ từ";
        } else {
            msg = "Đã gán " + processed + " thẻ từ vào " + cleaned.size() + " bộ thẻ thành công";
        }

        return Response.builder()
                .totalAssigned(processed)
                .message(msg)
                .build();
    }
}