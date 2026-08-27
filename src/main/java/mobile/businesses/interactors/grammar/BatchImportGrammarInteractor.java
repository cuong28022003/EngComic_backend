package mobile.businesses.interactors.grammar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.apis.grammar.dtos.BatchImportGrammarRequest;
import mobile.apis.grammar.dtos.GrammarPointDto;
import mobile.businesses.boundaries.grammar.BatchImportGrammarBoundary;
import mobile.databases.entities.grammar.GrammarPointEntity;
import mobile.databases.repositories.grammar.GrammarPointRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchImportGrammarInteractor implements BatchImportGrammarBoundary {

    private final GrammarPointRepository grammarPointRepository;
    private final GrammarMapper grammarMapper;

    @Override
    public Response execute(Request request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return Response.builder()
                    .insertedCount(0)
                    .updatedCount(0)
                    .total(0)
                    .data(Collections.emptyList())
                    .build();
        }

        int inserted = 0;
        int updated = 0;
        List<GrammarPointEntity> savedEntities = new ArrayList<>();

        for (BatchImportGrammarRequest.GrammarPointInput input : request.getItems()) {
            if (input.getTopic() == null || input.getTopic().trim().isEmpty()) {
                continue;
            }

            String topic = input.getTopic().trim();
            Optional<GrammarPointEntity> existingOpt = grammarPointRepository.findByTopicIgnoreCase(topic);

            GrammarPointEntity entity;
            if (existingOpt.isPresent()) {
                entity = existingOpt.get();
                updated++;
            } else {
                entity = GrammarPointEntity.builder()
                        .createdAt(new Date())
                        .build();
                inserted++;
            }

            entity.setTopic(topic);
            entity.setCategory(input.getCategory() != null ? input.getCategory().trim().toLowerCase() : "general");
            entity.setLevel(input.getLevel() != null ? input.getLevel().trim() : "intermediate");

            String summary = input.getSummary() != null ? input.getSummary() : input.getShortRule();
            entity.setSummary(summary);
            entity.setShortRule(summary);
            entity.setStructure(input.getStructure());
            entity.setSignalWords(input.getSignalWords() != null ? input.getSignalWords() : Collections.emptyList());
            entity.setCommonMistake(input.getCommonMistake());

            // Root Examples
            if (input.getExamples() != null) {
                List<GrammarPointEntity.GrammarExample> examples = input.getExamples().stream()
                        .map(e -> GrammarPointEntity.GrammarExample.builder()
                                .text(e.getText())
                                .translation(e.getTranslation())
                                .highlight(e.getHighlight())
                                .note(e.getNote())
                                .build())
                        .collect(Collectors.toList());
                entity.setExamples(examples);
            } else {
                entity.setExamples(Collections.emptyList());
            }

            // Multi Usages
            if (input.getUsages() != null) {
                List<GrammarPointEntity.GrammarUsage> usages = input.getUsages().stream()
                        .map(u -> GrammarPointEntity.GrammarUsage.builder()
                                .title(u.getTitle())
                                .structure(u.getStructure())
                                .explanation(u.getExplanation())
                                .signalWords(u.getSignalWords() != null ? u.getSignalWords() : Collections.emptyList())
                                .examples(u.getExamples() != null ?
                                        u.getExamples().stream()
                                                .map(e -> GrammarPointEntity.GrammarExample.builder()
                                                        .text(e.getText())
                                                        .translation(e.getTranslation())
                                                        .highlight(e.getHighlight())
                                                        .note(e.getNote())
                                                        .build())
                                                .collect(Collectors.toList()) : Collections.emptyList())
                                .note(u.getNote())
                                .build())
                        .collect(Collectors.toList());
                entity.setUsages(usages);
            } else {
                entity.setUsages(Collections.emptyList());
            }

            entity.setCommonMistakes(input.getCommonMistakes() != null ? input.getCommonMistakes() : Collections.emptyList());
            entity.setExamTips(input.getExamTips() != null ? input.getExamTips() : Collections.emptyList());

            // Comparisons
            if (input.getComparisons() != null) {
                List<GrammarPointEntity.GrammarComparison> comparisons = input.getComparisons().stream()
                        .map(c -> GrammarPointEntity.GrammarComparison.builder()
                                .compareWith(c.getCompareWith())
                                .coreDifference(c.getCoreDifference())
                                .currentExample(c.getCurrentExample())
                                .targetExample(c.getTargetExample())
                                .build())
                        .collect(Collectors.toList());
                entity.setComparisons(comparisons);
            } else {
                entity.setComparisons(Collections.emptyList());
            }

            entity.setSearchKeywords(input.getSearchKeywords() != null ? input.getSearchKeywords() : Collections.emptyList());
            entity.setUpdatedAt(new Date());

            savedEntities.add(grammarPointRepository.save(entity));
        }

        List<GrammarPointDto> dtos = savedEntities.stream()
                .map(grammarMapper::toDto)
                .collect(Collectors.toList());

        return Response.builder()
                .insertedCount(inserted)
                .updatedCount(updated)
                .total(savedEntities.size())
                .data(dtos)
                .build();
    }
}
