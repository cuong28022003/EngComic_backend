package mobile.businesses.boundaries.card;

import mobile.model.payload.request.card.CreateCardRequest;
import mobile.model.payload.response.card.CardResponse;

public interface CreateCardBoundary {
    CardResponse execute(Request request);

    record Request(CreateCardRequest payload, String currentUserId) {}
}
