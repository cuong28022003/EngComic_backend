package mobile.businesses.interactors.grammar;

import lombok.RequiredArgsConstructor;
import mobile.apis.grammar.dtos.CreateOrUpdateGrammarPointRequest;
import mobile.businesses.boundaries.grammar.SaveGrammarPointBoundary;
import mobile.databases.entities.grammar.GrammarPointEntity;
import mobile.databases.repositories.grammar.GrammarPointRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaveGrammarPointInteractor implements SaveGrammarPointBoundary {

    private final GrammarPointRepository grammarPointRepository;
    private final GrammarMapper grammarMapper;

    @Override
    public Response execute(Request request) {
        CreateOrUpdateGrammarPointRequest req = request.getData();
        if (req == null || req.getTopic() == null || req.getTopic().trim().isEmpty()) {
            throw new IllegalArgumentException("Topic không được để trống");
        }

        GrammarPointEntity entity;
        if (request.getId() != null && !request.getId().trim().isEmpty()) {
            entity = grammarPointRepository.findById(request.getId().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy điểm ngữ pháp với ID: " + request.getId()));
        } else {
            entity = GrammarPointEntity.builder()
                    .createdAt(new Date())
                    .build();
        }

        entity.setTopic(req.getTopic().trim());
        entity.setCategory(req.getCategory() != null ? req.getCategory().trim().toLowerCase() : "general");
        entity.setLevel(req.getLevel() != null ? req.getLevel().trim() : "intermediate");
        
        String summary = req.getSummary() != null ? req.getSummary() : req.getShortRule();
        entity.setSummary(summary);
        entity.setShortRule(summary);
        entity.setStructure(req.getStructure());
        entity.setSignalWords(req.getSignalWords() != null ? req.getSignalWords() : Collections.emptyList());
        entity.setCommonMistake(req.getCommonMistake());

        // Map Root Examples
        if (req.getExamples() != null) {
            List<GrammarPointEntity.GrammarExample> examples = req.getExamples().stream()
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

        // Map Multi Usages
        if (req.getUsages() != null) {
            List<GrammarPointEntity.GrammarUsage> usages = req.getUsages().stream()
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

        entity.setCommonMistakes(req.getCommonMistakes() != null ? req.getCommonMistakes() : Collections.emptyList());
        entity.setExamTips(req.getExamTips() != null ? req.getExamTips() : Collections.emptyList());

        // Map Comparisons
        if (req.getComparisons() != null) {
            List<GrammarPointEntity.GrammarComparison> comparisons = req.getComparisons().stream()
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

        entity.setSearchKeywords(req.getSearchKeywords() != null ? req.getSearchKeywords() : Collections.emptyList());
        entity.setUpdatedAt(new Date());

        GrammarPointEntity saved = grammarPointRepository.save(entity);

        return Response.builder()
                .data(grammarMapper.toDto(saved))
                .build();
    }
}
