package mobile.businesses.boundaries.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.card.dtos.DashboardResponseDto;
import org.springframework.data.domain.Pageable;

public interface GetCardDashboard {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String userId;
        private String search;
        private String status;
        private String topic;
        private Pageable pageable;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private DashboardResponseDto data;
    }
}
