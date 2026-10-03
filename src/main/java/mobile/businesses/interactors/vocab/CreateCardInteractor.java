package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.Handler.DuplicateWordException;
import mobile.apis.vocab.dtos.CreateCardRequest;
import mobile.businesses.boundaries.vocab.CreateCardBoundary;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
import mobile.domains.vocab.VocabRules;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateCardInteractor implements CreateCardBoundary {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public Response execute(Request request) {
        CreateCardRequest payload = request.getPayload();
        CardEntity cardEntity = cardMapper.toEntity(payload);
        String effectiveUserId = request.getCurrentUserId();
        if ((effectiveUserId == null || effectiveUserId.isBlank()) && cardEntity.getUserId() != null) {
            effectiveUserId = cardEntity.getUserId();
        }
        if (cardEntity.getUserId() == null && effectiveUserId != null) {
            cardEntity.setUserId(effectiveUserId);
        }
        if (effectiveUserId != null && cardEntity.getWord() != null && !cardEntity.getWord().isBlank()) {
            assertNoDuplicate(effectiveUserId, cardEntity);
        }

        CardEntity savedCard = cardRepository.save(cardEntity);
        return Response.builder()
                .card(cardMapper.toResponse(savedCard))
                .build();
    }

    /** @throws DuplicateWordException nếu từ + từ loại đã tồn tại trong kho của người dùng */
    private void assertNoDuplicate(String userId, CardEntity cardEntity) {
        List<CardEntity> existing = cardRepository.findAllByUserIdAndWordIgnoreCase(userId, cardEntity.getWord().trim());
        String newKey = VocabRules.vocabDedupKey(cardEntity.getWord(), cardEntity.getPartOfSpeech());
        for (CardEntity ex : existing) {
            String existingKey = VocabRules.vocabDedupKey(ex.getWord(), ex.getPartOfSpeech());
            if (existingKey.equals(newKey)) {
                throw new DuplicateWordException(
                        "Từ '" + ex.getWord() + "' (" + (ex.getPartOfSpeech() == null ? "chưa xác định" : ex.getPartOfSpeech()) + ") đã tồn tại trong kho từ vựng của bạn.");
            }
        }
    }
}

