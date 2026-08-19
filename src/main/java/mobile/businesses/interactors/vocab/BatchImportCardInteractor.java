package mobile.businesses.interactors.vocab;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.apis.vocab.dtos.BatchImportResponseDto;
import mobile.businesses.boundaries.vocab.BatchImportCard;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.entities.vocab.ExampleSentence;
import mobile.databases.entities.vocab.PendingItemEntity;
import mobile.databases.entities.vocab.WordRelation;
import mobile.databases.repositories.vocab.CardRepository;
import mobile.databases.repositories.vocab.PendingItemRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchImportCardInteractor implements BatchImportCard {

    private final CardRepository cardRepository;
    private final PendingItemRepository pendingItemRepository;
    private final CardMapper cardMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        String jsonContent = request.getJsonContent();
        String deckId = request.getDeckId();

        BatchImportResponseDto responseDto = new BatchImportResponseDto();
        if (jsonContent == null || jsonContent.trim().isEmpty()) {
            return Response.builder().data(responseDto).build();
        }

        List<Map<String, Object>> cardList;
        try {
            cardList = objectMapper.readValue(jsonContent, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.error("Failed to parse JSON input for batch import", e);
            return Response.builder().data(responseDto).build();
        }

        List<CardEntity> newCards = new ArrayList<>();
        Set<String> importedWords = new HashSet<>();

        for (Map<String, Object> map : cardList) {
            String word = (String) map.get("word");
            if (word == null || word.trim().isEmpty()) {
                word = (String) map.get("front");
            }
            if (word == null || word.trim().isEmpty()) {
                continue;
            }

            String cleanWord = word.trim();
            CardEntity card = new CardEntity();
            card.setUserId(userId);
            card.setDeckId(deckId);
            card.setFront(cleanWord);
            card.setBack((String) map.getOrDefault("vietnameseMeaning", map.get("back")));
            card.setIpa((String) map.get("ipa"));
            card.setPartOfSpeech((String) map.get("partOfSpeech"));
            card.setDefinitionEn((String) map.get("definitionEn"));
            card.setUsageNote((String) map.get("usageNote"));
            card.setTopic((String) map.get("topic"));

            if (map.containsKey("examples") && map.get("examples") instanceof List) {
                List<?> exList = (List<?>) map.get("examples");
                List<ExampleSentence> examples = new ArrayList<>();
                for (Object exObj : exList) {
                    if (exObj instanceof Map) {
                        Map<?, ?> exMap = (Map<?, ?>) exObj;
                        ExampleSentence sentence = new ExampleSentence(
                                UUID.randomUUID().toString(),
                                (String) exMap.get("en"),
                                (String) exMap.get("vi"),
                                (String) exMap.get("context")
                        );
                        examples.add(sentence);
                    }
                }
                card.setExamples(examples);
            }

            if (map.containsKey("relations") && map.get("relations") instanceof List) {
                List<?> relList = (List<?>) map.get("relations");
                List<WordRelation> relations = new ArrayList<>();
                for (Object relObj : relList) {
                    if (relObj instanceof Map) {
                        Map<?, ?> relMap = (Map<?, ?>) relObj;
                        WordRelation relation = new WordRelation(
                                (String) relMap.get("type"),
                                (String) relMap.get("word"),
                                (String) relMap.get("meaning")
                        );
                        relations.add(relation);
                    }
                }
                card.setRelations(relations);
            }

            card.setStage(0);
            card.setStatus("new");
            card.setInterval(0);
            card.setEaseFactor(2.5);
            card.setRepetition(0);
            card.setLapses(0);
            card.setWrongCount(0);
            card.setReviewCount(0);
            card.setLastReviewed(new Date());
            card.setNextReview(new Date());

            newCards.add(card);
            importedWords.add(cleanWord.toLowerCase());
        }

        if (!newCards.isEmpty()) {
            List<CardEntity> savedCards = cardRepository.saveAll(newCards);
            for (CardEntity sc : savedCards) {
                responseDto.getImported().add(cardMapper.toResponse(sc));
            }

            List<PendingItemEntity> pendingList = pendingItemRepository.findByUserIdAndStatus(userId, "pending");
            List<PendingItemEntity> toUpdate = new ArrayList<>();
            for (PendingItemEntity item : pendingList) {
                if (item.getContent() != null && importedWords.contains(item.getContent().trim().toLowerCase())) {
                    item.setStatus("imported");
                    toUpdate.add(item);
                }
            }
            if (!toUpdate.isEmpty()) {
                pendingItemRepository.saveAll(toUpdate);
            }
        }

        return Response.builder().data(responseDto).build();
    }
}
