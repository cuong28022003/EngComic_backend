package mobile.businesses.boundaries.pendingitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.pendingitem.dtos.PendingItemResponseDto;

public interface AddPendingItem {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String userId;
        private String content;
        private String sourceType;
        private String sourceCardId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private PendingItemResponseDto item;
    }
}
