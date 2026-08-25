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
        entity.setShortRule(req.getShortRule());
        entity.setStructure(req.getStructure());
        entity.setSignalWords(req.getSignalWords() != null ? req.getSignalWords() : Collections.emptyList());
        entity.setCommonMistake(req.getCommonMistake());

        if (req.getExamples() != null) {
            List<GrammarPointEntity.GrammarExample> examples = req.getExamples().stream()
                    .map(e -> GrammarPointEntity.GrammarExample.builder()
                            .text(e.getText())
                            .note(e.getNote())
                            .build())
                    .collect(Collectors.toList());
            entity.setExamples(examples);
        } else {
            entity.setExamples(Collections.emptyList());
        }

        entity.setSearchKeywords(req.getSearchKeywords() != null ? req.getSearchKeywords() : Collections.emptyList());
        entity.setUpdatedAt(new Date());

        GrammarPointEntity saved = grammarPointRepository.save(entity);

        return Response.builder()
                .data(grammarMapper.toDto(saved))
                .build();
    }
}
