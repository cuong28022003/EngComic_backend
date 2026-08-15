package mobile.businesses.boundaries.card;

import mobile.model.payload.response.card.CardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetUserCardsBoundary {
    Page<CardResponse> execute(Request request);

    record Request(String userId, String search, Pageable pageable) {}
}
