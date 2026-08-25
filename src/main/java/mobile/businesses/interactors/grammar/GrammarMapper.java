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

        return GrammarPointDto.builder()
                .id(entity.getId())
                .topic(entity.getTopic())
                .category(entity.getCategory())
                .shortRule(entity.getShortRule())
                .structure(entity.getStructure())
                .signalWords(entity.getSignalWords())
                .commonMistake(entity.getCommonMistake())
                .examples(entity.getExamples() != null ?
                        entity.getExamples().stream()
                                .map(e -> GrammarPointDto.GrammarExampleDto.builder()
                                        .text(e.getText())
                                        .note(e.getNote())
                                        .build())
                                .collect(Collectors.toList()) : Collections.emptyList())
                .searchKeywords(entity.getSearchKeywords())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
