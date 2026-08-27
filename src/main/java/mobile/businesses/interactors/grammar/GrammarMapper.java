package mobile.businesses.interactors.grammar;

import mobile.apis.grammar.dtos.GrammarPointDto;
import mobile.databases.entities.grammar.GrammarPointEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class GrammarMapper {

    public GrammarPointDto toDto(GrammarPointEntity entity) {
        if (entity == null) return null;

        // Auto fallback for summary & shortRule
        String summary = entity.getSummary();
        if (summary == null || summary.trim().isEmpty()) {
            summary = entity.getShortRule();
        }
        String shortRule = entity.getShortRule();
        if (shortRule == null || shortRule.trim().isEmpty()) {
            shortRule = summary;
        }

        return GrammarPointDto.builder()
                .id(entity.getId())
                .topic(entity.getTopic())
                .category(entity.getCategory())
                .level(entity.getLevel() != null ? entity.getLevel() : "intermediate")
                .summary(summary)
                .shortRule(shortRule)
                .structure(entity.getStructure())
                .signalWords(entity.getSignalWords())
                .commonMistake(entity.getCommonMistake())
                .examples(entity.getExamples() != null ?
                        entity.getExamples().stream()
                                .map(this::toExampleDto)
                                .collect(Collectors.toList()) : Collections.emptyList())
                .usages(entity.getUsages() != null ?
                        entity.getUsages().stream()
                                .map(this::toUsageDto)
                                .collect(Collectors.toList()) : Collections.emptyList())
                .commonMistakes(entity.getCommonMistakes() != null ? entity.getCommonMistakes() : Collections.emptyList())
                .examTips(entity.getExamTips() != null ? entity.getExamTips() : Collections.emptyList())
                .comparisons(entity.getComparisons() != null ?
                        entity.getComparisons().stream()
                                .map(this::toComparisonDto)
                                .collect(Collectors.toList()) : Collections.emptyList())
                .searchKeywords(entity.getSearchKeywords() != null ? entity.getSearchKeywords() : Collections.emptyList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private GrammarPointDto.GrammarUsageDto toUsageDto(GrammarPointEntity.GrammarUsage usage) {
        if (usage == null) return null;
        return GrammarPointDto.GrammarUsageDto.builder()
                .title(usage.getTitle())
                .structure(usage.getStructure())
                .explanation(usage.getExplanation())
                .signalWords(usage.getSignalWords() != null ? usage.getSignalWords() : Collections.emptyList())
                .examples(usage.getExamples() != null ?
                        usage.getExamples().stream()
                                .map(this::toExampleDto)
                                .collect(Collectors.toList()) : Collections.emptyList())
                .note(usage.getNote())
                .build();
    }

    private GrammarPointDto.GrammarExampleDto toExampleDto(GrammarPointEntity.GrammarExample example) {
        if (example == null) return null;
        return GrammarPointDto.GrammarExampleDto.builder()
                .text(example.getText())
                .translation(example.getTranslation())
                .highlight(example.getHighlight())
                .note(example.getNote())
                .build();
    }

    private GrammarPointDto.GrammarComparisonDto toComparisonDto(GrammarPointEntity.GrammarComparison comp) {
        if (comp == null) return null;
        return GrammarPointDto.GrammarComparisonDto.builder()
                .compareWith(comp.getCompareWith())
                .coreDifference(comp.getCoreDifference())
                .currentExample(comp.getCurrentExample())
                .targetExample(comp.getTargetExample())
                .build();
    }
}
