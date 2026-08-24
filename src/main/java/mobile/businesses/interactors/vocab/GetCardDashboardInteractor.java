package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.DashboardResponseDto;
import mobile.businesses.boundaries.vocab.GetCardDashboard;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GetCardDashboardInteractor implements GetCardDashboard {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final MongoTemplate mongoTemplate;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        String search = request.getSearch();
        String status = request.getStatus();
        String topic = request.getTopic();
        String deckId = request.getDeckId();
        Pageable pageable = request.getPageable();

        long totalCards = cardRepository.countByUserId(userId);
        long dueToday = cardRepository.countByUserIdAndNextReviewLessThanEqual(userId, new Date());
        long matureCount = cardRepository.countByUserIdAndStatus(userId, "mature");
        long learningCount = cardRepository.countByUserIdAndStatus(userId, "learning");
        long leechCount = cardRepository.countByUserIdAndStatus(userId, "leech");
        long newCount = cardRepository.countByUserIdAndStatus(userId, "new");

        Query query = new Query();
        if (userId != null) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }

        if (status != null && !status.trim().isEmpty()) {
            query.addCriteria(Criteria.where("status").is(status.trim()));
        }

        if (deckId != null && !deckId.trim().isEmpty()) {
            if ("unassigned".equalsIgnoreCase(deckId.trim())) {
                query.addCriteria(new Criteria().orOperator(
                        Criteria.where("deckId").is(null),
                        Criteria.where("deckId").is("")
                ));
            } else {
                query.addCriteria(Criteria.where("deckId").is(deckId.trim()));
            }
        }

        // Fuzzy / Partial match on topic
        if (topic != null && !topic.trim().isEmpty()) {
            query.addCriteria(Criteria.where("topic").regex(Pattern.quote(topic.trim()), "i"));
        }

        // Partial match on word, meaning, front, back
        if (search != null && !search.trim().isEmpty()) {
            String term = Pattern.quote(search.trim());
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("word").regex(term, "i"),
                    Criteria.where("meaning").regex(term, "i"),
                    Criteria.where("front").regex(term, "i"),
                    Criteria.where("back").regex(term, "i")
            ));
        }

        long totalFiltered = mongoTemplate.count(query, CardEntity.class);
        query.with(pageable);
        List<CardEntity> cards = mongoTemplate.find(query, CardEntity.class);
        Page<CardEntity> cardPage = new PageImpl<>(cards, pageable, totalFiltered);

        DashboardResponseDto responseDto = DashboardResponseDto.builder()
                .totalCards(totalCards)
                .dueToday(dueToday)
                .matureCount(matureCount)
                .learningCount(learningCount)
                .leechCount(leechCount)
                .newCount(newCount)
                .cards(cardPage.map(cardMapper::toResponse))
                .build();

        return Response.builder().data(responseDto).build();
    }
}
