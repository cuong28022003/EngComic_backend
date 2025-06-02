package mobile.model.payload.request.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {
    private String userId;
    private String comicId;
    private String reason;
    private String status; // Thêm trường này để đồng bộ với frontend
}