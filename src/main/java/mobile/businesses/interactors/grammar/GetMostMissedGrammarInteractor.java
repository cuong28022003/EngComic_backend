package mobile.businesses.interactors.grammar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.apis.grammar.dtos.MostMissedGrammarDto;
import mobile.businesses.boundaries.grammar.GetMostMissedGrammarBoundary;
import mobile.databases.entities.grammar.GrammarPointEntity;
import mobile.databases.entities.reader.ToeicReviewItemEntity;
import mobile.databases.repositories.grammar.GrammarPointRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetMostMissedGrammarInteractor implements GetMostMissedGrammarBoundary {

    private final MongoTemplate mongoTemplate;
    private final GrammarPointRepository grammarPointRepository;
    private final GrammarMapper grammarMapper;

    @Override
    public Response execute(Request request) {
        int limit = request.getLimit() > 0 ? request.getLimit() : 5;

        Criteria criteria = Criteria.where("errorType").is("grammar");
        if (request.getUserId() != null && !request.getUserId().trim().isEmpty()) {
            criteria = criteria.and("userId").is(request.getUserId().trim());
        }

        Aggregation aggregation = newAggregation(
                match(criteria),
                group("relatedGrammarTopic").count().as("count"),
                sort(Sort.Direction.DESC, "count"),
                limit(limit)
        );

        AggregationResults<TopicCountResult> results = mongoTemplate.aggregate(
                aggregation,
                ToeicReviewItemEntity.class,
                TopicCountResult.class
        );

        List<MostMissedGrammarDto> dtoList = new ArrayList<>();

        for (TopicCountResult res : results.getMappedResults()) {
            String topicName = res.getId();
            if (topicName == null || topicName.trim().isEmpty()) {
                continue;
            }

            Optional<GrammarPointEntity> gpOpt = grammarPointRepository.findByTopicIgnoreCase(topicName.trim());
            GrammarPointEntity gp = gpOpt.orElse(null);

            dtoList.add(MostMissedGrammarDto.builder()
                    .topic(topicName)
                    .category(gp != null ? gp.getCategory() : "grammar")
                    .count(res.getCount())
                    .grammarPoint(grammarMapper.toDto(gp))
                    .build());
        }

        return Response.builder()
                .data(dtoList)
                .build();
    }

    @lombok.Data
    private static class TopicCountResult {
        private String id;
        private long count;
    }
}
