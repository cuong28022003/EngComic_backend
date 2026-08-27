package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.vocab.GetUserTopicsBoundary;
import mobile.databases.entities.vocab.CardEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserTopicsInteractor implements GetUserTopicsBoundary {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<String> execute(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return List.of();
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .and("topic").ne(null).ne(""));

        List<String> distinctTopics = mongoTemplate.findDistinct(query, "topic", CardEntity.class, String.class);

        return distinctTopics.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }
}
