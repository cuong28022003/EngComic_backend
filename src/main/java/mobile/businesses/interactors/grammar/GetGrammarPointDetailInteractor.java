package mobile.businesses.interactors.grammar;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.grammar.GetGrammarPointDetailBoundary;
import mobile.databases.entities.grammar.GrammarPointEntity;
import mobile.databases.repositories.grammar.GrammarPointRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetGrammarPointDetailInteractor implements GetGrammarPointDetailBoundary {

    private final GrammarPointRepository grammarPointRepository;
    private final GrammarMapper grammarMapper;

    @Override
    public Response execute(Request request) {
        GrammarPointEntity entity = null;

        if (request.getId() != null && !request.getId().trim().isEmpty()) {
            entity = grammarPointRepository.findById(request.getId().trim()).orElse(null);
        } else if (request.getTopic() != null && !request.getTopic().trim().isEmpty()) {
            entity = grammarPointRepository.findByTopicIgnoreCase(request.getTopic().trim()).orElse(null);
        }

        return Response.builder()
                .data(grammarMapper.toDto(entity))
                .build();
    }
}
