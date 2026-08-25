package mobile.businesses.interactors.grammar;

import lombok.RequiredArgsConstructor;
import mobile.apis.grammar.dtos.GrammarPointDto;
import mobile.businesses.boundaries.grammar.GetGrammarPointsBoundary;
import mobile.databases.entities.grammar.GrammarPointEntity;
import mobile.databases.repositories.grammar.GrammarPointRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetGrammarPointsInteractor implements GetGrammarPointsBoundary {

    private final GrammarPointRepository grammarPointRepository;
    private final GrammarMapper grammarMapper;

    @Override
    public Response execute(Request request) {
        List<GrammarPointEntity> entities;

        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            entities = grammarPointRepository.searchGrammarPoints(request.getKeyword().trim());
        } else if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            entities = grammarPointRepository.findByCategoryIgnoreCaseOrderByTopicAsc(request.getCategory().trim());
        } else {
            entities = grammarPointRepository.findAll(Sort.by(Sort.Direction.ASC, "category", "topic"));
        }

        List<GrammarPointDto> dtos = entities.stream()
                .map(grammarMapper::toDto)
                .collect(Collectors.toList());

        return Response.builder()
                .data(dtos)
                .total(dtos.size())
                .build();
    }
}
