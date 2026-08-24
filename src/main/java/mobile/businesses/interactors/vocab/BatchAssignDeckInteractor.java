package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.vocab.BatchAssignDeckBoundary;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchAssignDeckInteractor implements BatchAssignDeckBoundary {

    private final MongoTemplate mongoTemplate;
    private final CardRepository cardRepository;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        List<String> cardIds = request.getCardIds();
        String deckId = request.getDeckId();

        if (cardIds == null || cardIds.isEmpty()) {
            return Response.builder()
                    .totalAssigned(0)
                    .message("Không có thẻ từ nào được chọn")
                    .build();
        }

        // Target deckId: null if empty or unassigned
        String targetDeckId = (deckId != null && !deckId.isBlank() && !"unassigned".equalsIgnoreCase(deckId.trim()))
                ? deckId.trim()
                : null;

        Query query = new Query(Criteria.where("id").in(cardIds));
        if (userId != null) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }

        Update update = new Update()
                .set("deckId", targetDeckId)
                .set("updatedAt", new Date());

        com.mongodb.client.result.UpdateResult result = mongoTemplate.updateMulti(query, update, CardEntity.class);
        long modifiedCount = result.getModifiedCount();

        String msg = targetDeckId != null
                ? "Đã gán " + modifiedCount + " thẻ từ vào bộ thẻ thành công"
                : "Đã hủy gán bộ thẻ cho " + modifiedCount + " thẻ từ";

        return Response.builder()
                .totalAssigned((int) modifiedCount)
                .message(msg)
                .build();
    }
}
