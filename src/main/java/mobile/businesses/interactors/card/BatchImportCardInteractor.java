package mobile.businesses.interactors.card;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.apis.card.dtos.BatchImportResponseDto;
import mobile.businesses.boundaries.card.BatchImportCard;
import mobile.databases.entities.card.CardEntity;
import mobile.databases.entities.card.ExampleSentence;
import mobile.databases.entities.card.WordRelation;
import mobile.databases.entities.pendingitem.PendingItemEntity;
import mobile.databases.repositories.card.CardRepository;
import mobile.databases.repositories.pendingitem.PendingItemRepository;
import mobile.databases.services.card.CardDatabaseService;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchImportCardInteractor implements BatchImportCard {

    private final CardRepository cardRepository;
    private final PendingItemRepository pendingItemRepository;
    private final CardDatabaseService cardDatabaseService;
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

        String cleanedJson = jsonContent.trim();
        if (cleanedJson.startsWith("```json")) {
            cleanedJson = cleanedJson.substring(7);
        } else if (cleanedJson.startsWith("```")) {
            cleanedJson = cleanedJson.substring(3);
        }
        if (cleanedJson.endsWith("```")) {
            cleanedJson = cleanedJson.substring(0, cleanedJson.length() - 3);
        }
        cleanedJson = cleanedJson.trim();

        List<Map<String, Object>> entries;
        try {
            entries = objectMapper.readValue(cleanedJson, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.error("Failed to parse batch JSON: {}", e.getMessage());
            responseDto.getErrors().add(new BatchImportResponseDto.ImportError("JSON_PARSE_ERROR", List.of(e.getMessage())));
            return Response.builder().data(responseDto).build();
        }

        List<CardEntity> newCards = new ArrayList<>();
        Set<String> importedWords = new HashSet<>();

        for (Map<String, Object> map : entries) {
            String word = (String) map.get("word");
            String meaningVi = (String) map.get("meaning_vi");

            List<String> missingFields = new ArrayList<>();
            if (word == null || word.trim().isEmpty()) {
                missingFields.add("word");
            }
            if (meaningVi == null || meaningVi.trim().isEmpty()) {
                missingFields.add("meaning_vi");
            }

            if (!missingFields.isEmpty()) {
                responseDto.getErrors().add(new BatchImportResponseDto.ImportError(word != null ? word : "UNKNOWN", missingFields));
                continue;
            }

            String cleanWord = word.trim();
            Optional<CardEntity> existing = cardRepository.findByUserIdAndFrontIgnoreCase(userId, cleanWord);
            if (existing.isPresent()) {
                responseDto.getSkipped().add(cleanWord);
                continue;
            }

            CardEntity card = new CardEntity();
            card.setUserId(userId);
            card.setDeckId(deckId);
            card.setFront(cleanWord);
            card.setBack(meaningVi.trim());
            card.setIpa((String) map.get("ipa"));
            card.setPartOfSpeech((String) map.get("part_of_speech"));
            card.setDefinitionEn((String) map.get("definition_en"));
            card.setUsageNote((String) map.get("usage_note"));
            card.setTopic((String) map.get("topic"));
            card.setAudio((String) map.get("audio_url"));

            Object examplesObj = map.get("examples");
            if (examplesObj instanceof List) {
                List<?> exList = (List<?>) examplesObj;
                List<ExampleSentence> examples = new ArrayList<>();
                for (Object item : exList) {
                    if (item instanceof Map) {
                        Map<?, ?> exMap = (Map<?, ?>) item;
                        String text = (String) exMap.get("text");
                        String formality = (String) exMap.get("formality");
                        String translation = (String) exMap.get("translation");
                        if (text != null && !text.trim().isEmpty()) {
                            ExampleSentence ex = new ExampleSentence();
                            ex.setSentence(text.trim());
                            ex.setFormality(formality != null ? formality : "formal");
                            ex.setTranslation(translation);
                            examples.add(ex);
                        }
                    }
                }
                card.setExamples(examples);
            }

            Object relationsObj = map.get("relations");
            if (relationsObj instanceof List) {
                List<?> relList = (List<?>) relationsObj;
                List<WordRelation> relations = new ArrayList<>();
                for (Object item : relList) {
                    if (item instanceof Map) {
                        Map<?, ?> relMap = (Map<?, ?>) item;
                        String text = (String) relMap.get("text");
                        String type = (String) relMap.get("type");
                        String pos = (String) relMap.get("pos");
                        if (text != null && !text.trim().isEmpty()) {
                            WordRelation rel = new WordRelation();
                            rel.setRelatedText(text.trim());
                            rel.setRelationType(type != null ? type : "family");
                            rel.setPos(pos);
                            relations.add(rel);
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
            for (CardEntity sc : savedCards) {
                responseDto.getImported().add(cardMapper.toResponse(sc));
            }

            cardDatabaseService.autoLinkRelations(userId);

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
