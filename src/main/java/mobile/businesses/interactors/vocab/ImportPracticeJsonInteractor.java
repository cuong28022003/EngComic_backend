package mobile.businesses.interactors.vocab;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.businesses.boundaries.vocab.ImportPracticeJsonBoundary;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.entities.vocab.CardExercisePackage;
import mobile.databases.repositories.vocab.CardRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportPracticeJsonInteractor implements ImportPracticeJsonBoundary {

    private final CardRepository cardRepository;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Response execute(Request request) {
        String jsonContent = request.getJsonContent();
        String userId = request.getUserId();
        String deckId = request.getDeckId();

        if (jsonContent == null || jsonContent.isBlank()) {
            return Response.builder()
                    .totalProcessed(0)
                    .successCount(0)
                    .message("Nội dung JSON rỗng")
                    .build();
        }

        // Clean markdown blocks if present
        String cleanedJson = jsonContent.trim();
        if (cleanedJson.startsWith("```")) {
            int firstNewline = cleanedJson.indexOf("\n");
            int lastBackticks = cleanedJson.lastIndexOf("```");
            if (firstNewline != -1 && lastBackticks > firstNewline) {
                cleanedJson = cleanedJson.substring(firstNewline + 1, lastBackticks).trim();
            }
        }

        int successCount = 0;
        int totalProcessed = 0;

        try {
            JsonNode rootNode = objectMapper.readTree(cleanedJson);
            if (!rootNode.isArray()) {
                return Response.builder()
                        .totalProcessed(0)
                        .successCount(0)
                        .message("Định dạng JSON không phải là mảng (Array)")
                        .build();
            }

            // Get cards for user (and deck if specified)
            List<CardEntity> cards;
            if (deckId != null && !deckId.isBlank()) {
                cards = cardRepository.findByUserIdAndDeckId(userId, deckId);
            } else {
                cards = cardRepository.findByUserId(userId);
            }

            Map<String, CardEntity> cardMap = new HashMap<>();
            for (CardEntity card : cards) {
                if (card.getWord() != null) {
                    cardMap.put(card.getWord().trim().toLowerCase(), card);
                }
            }

            int skippedCount = 0;
            for (JsonNode itemNode : rootNode) {
                totalProcessed++;
                String word = itemNode.path("word").asText("").trim().toLowerCase();
                if (word.isBlank()) continue;

                CardEntity targetCard = cardMap.get(word);
                if (targetCard == null) {
                    // Try to find globally for user if not found in deck
                    Optional<CardEntity> globalOpt = cardRepository.findByUserIdAndWordIgnoreCase(userId, word);
                    if (globalOpt.isPresent()) {
                        targetCard = globalOpt.get();
                    }
                }

                if (targetCard != null) {
                    // If card already has exercisePackage, skip it
                    if (targetCard.getExercisePackage() != null) {
                        skippedCount++;
                        continue;
                    }

                    CardExercisePackage pkg = parseExercisePackage(itemNode);
                    targetCard.setExercisePackage(pkg);
                    targetCard.setUpdateAt(new Date());
                    cardRepository.save(targetCard);
                    successCount++;
                }
            }

            String msg = String.format("Đã import thành công %d bộ bài tập mới (%d từ đã có sẵn bài tập được bỏ qua)",
                    successCount, skippedCount);
            return Response.builder()
                    .totalProcessed(totalProcessed)
                    .successCount(successCount)
                    .message(msg)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi khi parse hoặc import JSON bài tập", e);
            return Response.builder()
                    .totalProcessed(totalProcessed)
                    .successCount(successCount)
                    .message("Lỗi cú pháp JSON: " + e.getMessage())
                    .build();
        }
    }

    private CardExercisePackage parseExercisePackage(JsonNode node) {
        CardExercisePackage.CardExercisePackageBuilder builder = CardExercisePackage.builder();

        // Level 1: Recognition
        JsonNode l1Node = node.path("level1_recognition");
        if (!l1Node.isMissingNode() && !l1Node.isNull()) {
            String q = l1Node.path("question").asText("Chọn nghĩa đúng:");
            List<CardExercisePackage.ExerciseOption> options = new ArrayList<>();
            JsonNode optionsNode = l1Node.path("options");
            if (optionsNode.isArray()) {
                for (JsonNode opt : optionsNode) {
                    boolean isCorrect = parseBoolean(opt, "isCorrect", "is_correct", "correct", "is_true", "isTrue");
                    options.add(CardExercisePackage.ExerciseOption.builder()
                            .text(opt.path("text").asText(""))
                            .isCorrect(isCorrect)
                            .build());
                }
                // Fallback: if no option is marked correct, mark the 1st option as correct
                if (!options.isEmpty() && options.stream().noneMatch(CardExercisePackage.ExerciseOption::isCorrect)) {
                    options.get(0).setCorrect(true);
                }
            }
            builder.level1Recognition(CardExercisePackage.Level1Recognition.builder()
                    .question(q)
                    .options(options)
                    .build());
        }

        // Level 2: Context
        JsonNode l2Node = node.path("level2_context");
        if (!l2Node.isMissingNode() && !l2Node.isNull()) {
            String q = l2Node.path("question").asText("Điền vào chỗ trống:");
            String sentence = l2Node.path("sentence").asText("");
            String collocationNote = l2Node.path("collocationNote").asText(l2Node.path("collocation_note").asText(""));
            List<CardExercisePackage.ExerciseOption> options = new ArrayList<>();
            JsonNode optionsNode = l2Node.path("options");
            if (optionsNode.isArray()) {
                for (JsonNode opt : optionsNode) {
                    boolean isCorrect = parseBoolean(opt, "isCorrect", "is_correct", "correct", "is_true", "isTrue");
                    options.add(CardExercisePackage.ExerciseOption.builder()
                            .text(opt.path("text").asText(""))
                            .isCorrect(isCorrect)
                            .build());
                }
                // Fallback: if no option is marked correct, mark the 1st option as correct
                if (!options.isEmpty() && options.stream().noneMatch(CardExercisePackage.ExerciseOption::isCorrect)) {
                    options.get(0).setCorrect(true);
                }
            }
            builder.level2Context(CardExercisePackage.Level2Context.builder()
                    .question(q)
                    .sentence(sentence)
                    .options(options)
                    .collocationNote(collocationNote)
                    .build());
        }

        // Level 3: Production
        JsonNode l3Node = node.path("level3_production");
        if (!l3Node.isMissingNode() && !l3Node.isNull()) {
            String prompt = l3Node.path("prompt").asText("Sắp xếp các mảnh từ thành câu hoàn chỉnh:");
            String correctSentence = l3Node.path("correctSentence").asText(l3Node.path("correct_sentence").asText(""));
            String vietnameseMeaning = l3Node.path("vietnameseMeaning").asText(l3Node.path("vietnamese_meaning").asText(""));
            List<String> shuffledWords = new ArrayList<>();
            JsonNode wordsNode = l3Node.path("shuffledWords");
            if (wordsNode.isMissingNode() || wordsNode.isNull()) {
                wordsNode = l3Node.path("shuffled_words");
            }
            if (wordsNode.isArray()) {
                for (JsonNode w : wordsNode) {
                    shuffledWords.add(w.asText(""));
                }
            }
            builder.level3Production(CardExercisePackage.Level3Production.builder()
                    .prompt(prompt)
                    .correctSentence(correctSentence)
                    .vietnameseMeaning(vietnameseMeaning)
                    .shuffledWords(shuffledWords)
                    .build());
        }

        // Level 4: Realworld
        JsonNode l4Node = node.path("level4_realworld");
        if (!l4Node.isMissingNode() && !l4Node.isNull()) {
            String situation = l4Node.path("situation").asText("");
            String sampleResponse = l4Node.path("sampleResponse").asText(l4Node.path("sample_response").asText(""));
            String keyTakeaways = l4Node.path("keyTakeaways").asText(l4Node.path("key_takeaways").asText(""));
            builder.level4Realworld(CardExercisePackage.Level4Realworld.builder()
                    .situation(situation)
                    .sampleResponse(sampleResponse)
                    .keyTakeaways(keyTakeaways)
                    .build());
        }

        return builder.build();
    }

    private boolean parseBoolean(JsonNode node, String... keys) {
        if (node == null || node.isNull()) return false;
        for (String k : keys) {
            JsonNode val = node.path(k);
            if (!val.isMissingNode() && !val.isNull()) {
                if (val.isBoolean()) return val.asBoolean();
                if (val.isTextual()) {
                    String str = val.asText().trim().toLowerCase();
                    return "true".equals(str) || "1".equals(str) || "yes".equals(str);
                }
                if (val.isNumber()) return val.asInt() == 1;
            }
        }
        return false;
    }
}
