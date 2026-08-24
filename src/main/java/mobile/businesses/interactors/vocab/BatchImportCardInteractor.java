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
            String cleaned = jsonContent.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();

            cardList = objectMapper.readValue(cleaned, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.error("Failed to parse JSON input for batch import", e);
            return Response.builder().data(responseDto).build();
        }

        List<CardEntity> newCards = new ArrayList<>();
        Set<String> importedWords = new HashSet<>();

        for (Map<String, Object> map : cardList) {
            String word = getString(map, "word", "front", "text");
            if (word == null || word.trim().isEmpty()) {
                continue;
            }

            String cleanWord = word.trim();
            CardEntity card = new CardEntity();
            card.setUserId(userId);
            card.setDeckId(deckId);
            card.setWord(cleanWord);
            card.setMeaning(getString(map, "meaning", "meaning_vi", "meaningVi", "vietnameseMeaning", "back", "vietnamese_meaning"));
            card.setIpa(getString(map, "ipa", "IPA"));
            card.setPartOfSpeech(getString(map, "partOfSpeech", "part_of_speech", "pos"));
            card.setDefinitionEn(getString(map, "definitionEn", "definition_en", "definition"));
            card.setUsageNote(getString(map, "usageNote", "usage_note", "note"));
            card.setTopic(getString(map, "topic", "category"));

            if (map.containsKey("examples") && map.get("examples") instanceof List) {
                List<?> exList = (List<?>) map.get("examples");
                List<ExampleSentence> examples = new ArrayList<>();
                for (Object exObj : exList) {
                    if (exObj instanceof Map) {
                        Map<?, ?> exMap = (Map<?, ?>) exObj;
                        String text = getString(exMap, "text", "sentence", "en");
                        String translation = getString(exMap, "translation", "vi", "meaning");
                        String formality = getString(exMap, "formality", "context");

                        if (text != null && !text.trim().isEmpty()) {
                            ExampleSentence sentence = new ExampleSentence(
                                    UUID.randomUUID().toString(),
                                    text.trim(),
                                    translation != null ? translation.trim() : null,
                                    formality != null ? formality.trim() : null
                            );
                            examples.add(sentence);
                        }
                    } else if (exObj instanceof String) {
                        String exStr = (String) exObj;
                        if (!exStr.trim().isEmpty()) {
                            examples.add(new ExampleSentence(UUID.randomUUID().toString(), exStr.trim(), null, null));
                        }
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
                        String relText = getString(relMap, "text", "word", "relatedText", "related_text");
                        String relType = getString(relMap, "type", "relationType", "relation_type");
                        String relPos = getString(relMap, "pos", "partOfSpeech", "part_of_speech", "meaning");

                        if (relText != null && !relText.trim().isEmpty()) {
                            WordRelation relation = new WordRelation(
                                    relText.trim(),
                                    relType != null ? relType.trim() : "family",
                                    relPos != null ? relPos.trim() : null
                            );
                            relations.add(relation);
                        }
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

            // Auto-link relations with existing and new cards
            List<CardEntity> allUserCards = cardRepository.findByUserId(userId);
            Map<String, String> wordToIdMap = new HashMap<>();
            for (CardEntity c : allUserCards) {
                if (c.getWord() != null) {
                    wordToIdMap.put(c.getWord().trim().toLowerCase(), c.getId());
                }
            }

            for (CardEntity sc : savedCards) {
                boolean modified = false;
                if (sc.getRelations() != null) {
                    for (WordRelation r : sc.getRelations()) {
                        if (r.getRelatedCardId() == null && r.getText() != null) {
                            String targetId = wordToIdMap.get(r.getText().trim().toLowerCase());
                            if (targetId != null && !targetId.equals(sc.getId())) {
                                r.setRelatedCardId(targetId);
                                modified = true;
                            }
                        }
                    }
                }
                if (modified) {
                    cardRepository.save(sc);
                }
                responseDto.getImported().add(cardMapper.toResponse(sc));
            }

            List<PendingItemEntity> pendingList = pendingItemRepository.findByUserIdAndStatus(userId, "pending");
            List<PendingItemEntity> toDelete = new ArrayList<>();
            for (PendingItemEntity item : pendingList) {
                if (item.getContent() != null && importedWords.contains(item.getContent().trim().toLowerCase())) {
                    toDelete.add(item);
                }
            }
            if (!toDelete.isEmpty()) {
                pendingItemRepository.deleteAll(toDelete);
            }
        }

        return Response.builder().data(responseDto).build();
    }

    private String getString(Map<?, ?> map, String... keys) {
        if (map == null) return null;
        for (String key : keys) {
            Object val = map.get(key);
            if (val != null) {
                String str = String.valueOf(val).trim();
                if (!str.isEmpty() && !"null".equalsIgnoreCase(str)) {
                    return str;
                }
            }
        }
        return null;
    }
}
