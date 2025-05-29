package mobile.model.payload.request.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {
    private String comicId;
    private String userId;
    private String reason;
}