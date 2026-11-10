package mobile.businesses.boundaries.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.apis.user.dtos.CheckInResponseDto;

public interface GetCheckInStatus {
    Response execute(Request request);

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class Request {
        private String userId;
        // Nếu có year + month thì lọc history theo tháng, ngược lại trả 30 ngày gần nhất.
        private Integer year;
        private Integer month;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class Response {
        private CheckInResponseDto result;
    }
}
